package com.fashion.app.service.order;

import com.fashion.app.dto.request.CancelOrderRequestDTO;
import com.fashion.app.dto.request.PlaceOrderRequestDTO;
import com.fashion.app.dto.response.*;
import com.fashion.app.exception.BadRequestException;
import com.fashion.app.exception.ResourceNotFoundException;
import com.fashion.app.model.*;
import com.fashion.app.model.enums.*;
import com.fashion.app.repository.*;
import com.fashion.app.service.notification.NotificationService;
import com.fashion.app.service.payment.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final MomoService momoService;
    private final NotificationService notificationService;

    // ĐẶT HÀNG

    @Override
    @Transactional
    public PlaceOrderResponseDTO placeOrder(PlaceOrderRequestDTO dto) {
        // 0. Tìm người dùng
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));

        // 1. Lấy danh sách sản phẩm trong giỏ hàng
        List<CartItem> cartItems = cartItemRepository.findAllById(dto.getCartItemIds());

        if (cartItems.isEmpty() || cartItems.size() != dto.getCartItemIds().size()) {
            throw new BadRequestException("Giỏ hàng rỗng hoặc các mục đã bị xóa!");
        }
        for (CartItem item : cartItems) {
            if (!item.getUser().getId().equals(user.getId())) {
                throw new BadRequestException("Bạn không có quyền thanh toán các mặt hàng trong giỏ hàng này!");
            }
        }

        // 2. Tính tổng tiền & Xác thực tồn kho
        double totalAmount = 0.0;
        for (CartItem item : cartItems) {
            ProductVariant variant = item.getProductVariant();
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException(
                        "Sản phẩm " + variant.getProduct().getName() + " không đủ số lượng tồn kho!");
            }
            totalAmount += variant.getPrice() * item.getQuantity();
        }

        // 3. Áp dụng mã giảm giá (Nếu có)
        Coupon appliedCoupon = null;
        if (dto.getCouponCode() != null && !dto.getCouponCode().trim().isEmpty()) {
            Coupon coupon = couponRepository.findByCodeAndActiveTrue(dto.getCouponCode().trim())
                    .orElseThrow(() -> new BadRequestException("Mã giảm giá không tồn tại hoặc đã bị khóa!"));

            if (coupon.getExpiryDate().isBefore(java.time.Instant.now()) ||
                    coupon.getStartDate().isAfter(java.time.Instant.now())) {
                throw new BadRequestException("Mã giảm giá không hợp lệ hoặc đã hết hạn!");
            }

            if (coupon.getMinOrderAmount() != null && totalAmount < coupon.getMinOrderAmount()) {
                throw new BadRequestException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã giảm giá này!");
            }

            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                totalAmount -= totalAmount * (coupon.getDiscountValue() / 100.0);
            } else {
                totalAmount -= coupon.getDiscountValue();
            }

            if (totalAmount < 0)
                totalAmount = 0.0;

            // 3.5. Kiểm tra xem người dùng đã dùng mã này chưa
            userCouponRepository.findByUserIdAndCouponCode(user.getId(), dto.getCouponCode().trim())
                    .ifPresent(uc -> {
                        if (uc.isUsed()) {
                            throw new BadRequestException("Mã giảm giá này đã được sử dụng!");
                        }
                    });

            appliedCoupon = coupon;
        }

        // 4. Khởi tạo & Lưu Object Order chính
        Order order = new Order();
        order.setOrderDate(new Date().toInstant());
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(dto.getShippingAddress());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setType(OrderType.ONLINE);
        order.setUser(user);
        order.setCoupon(appliedCoupon); // Gán mã coupon để lưu reference

        OrderStatus initialStatus = (dto.getPaymentMethod() == PaymentMethod.COD)
                ? OrderStatus.PENDING_CONFIRMATION
                : OrderStatus.PENDING_PAYMENT;
        order.setStatus(initialStatus);

        order = orderRepository.save(order);

        // 5. Khởi tạo OrderItem (mỗi item có status riêng) và Trừ Kho

        for (CartItem item : cartItems) {
            ProductVariant variant = item.getProductVariant();

            OrderItem orderItem = OrderItem.builder()
                    .productVariant(variant)
                    .quantity((long) item.getQuantity())
                    .price(variant.getPrice())
                    .productName(variant.getProduct().getName())
                    .order(order)
                    .status(initialStatus)
                    .build();
            orderItemRepository.save(orderItem);

            variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
            productVariantRepository.save(variant);

            // Tự động kiểm tra và cập nhật trạng thái cha nếu Hết Hàng
            Product parentProduct = variant.getProduct();
            boolean hasStock = parentProduct.getVariants().stream()
                    .anyMatch(v -> v.getStockQuantity() != null && v.getStockQuantity() > 0);

            if (!hasStock && parentProduct.getStatus() == ProductStatus.ACTIVE) {
                parentProduct.setStatus(ProductStatus.OUT_OF_STOCK);
            }
        }

        // 6. Xóa các mục này khỏi giỏ hàng
        cartItemRepository.deleteAll(cartItems);

        // 7. Xử lý Thanh toán trực tuyến (MoMo)
        String paymentUrl = null;
        if (order.getPaymentMethod() == PaymentMethod.MOMO) {
            paymentUrl = momoService.createPaymentUrl(order.getId(), order.getTotalAmount());
        }

        // 7.5. Đánh dấu Coupon đã sử dụng (Nếu có)
        if (dto.getCouponCode() != null && !dto.getCouponCode().trim().isEmpty()) {
            userCouponRepository.findByUserIdAndCouponCode(user.getId(), dto.getCouponCode().trim())
                    .ifPresent(uc -> {
                        uc.setUsed(true);
                        userCouponRepository.save(uc);
                    });
        }

        // 9. Gửi thông báo
        notificationService.createNotification(
                user.getId(),
                "Đặt hàng thành công",
                "Đơn hàng #" + order.getId() + " của bạn đã được khởi tạo thành công.",
                "SUCCESS",
                order.getId());

        // 10. Trả về kết quả
        return PlaceOrderResponseDTO.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .paymentUrl(paymentUrl)
                .message(order.getPaymentMethod() == PaymentMethod.MOMO
                        ? "Đơn hàng đã được khởi tạo. Vui lòng thanh toán trong vòng 10 phút."
                        : "Đặt hàng thành công! Đơn hàng của bạn đang chờ xác nhận.")
                .build();
    }

    @Override
    @Transactional
    public void revertInventory(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại!"));

        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity().intValue());
            productVariantRepository.save(variant);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponseDTO> getCustomerOrderHistory(Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponseDTO> getMyOrders(Long userId, List<OrderStatus> statuses, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemSummaryDTO> getMyOrderItems(Long userId, List<OrderStatus> statuses, Boolean reviewed, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponseDTO getMyOrderDetail(Long userId, Long orderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public MessageResponseDTO cancelOrder(Long userId, Long orderId, CancelOrderRequestDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại!"));

        // 1. Kiểm tra quyền sở hữu
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền hủy đơn hàng này!");
        }

        Set<OrderStatus> cancellableStatuses = Set.of(
                OrderStatus.PENDING_PAYMENT,
                OrderStatus.PENDING_CONFIRMATION,
                OrderStatus.PAID,
                OrderStatus.PROCESSING);

        // 2. Kiểm tra trạng thái đơn hàng
        if (!cancellableStatuses.contains(order.getStatus())) {
            throw new BadRequestException("Đơn hàng đang ở trạng thái " + order.getStatus() + ", không thể hủy!");
        }

        boolean hasRefund = false;
        boolean refundFailed = false;

        // 3. Cập nhật trạng thái Order chính thành CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // 4. Xử lý từng OrderItem
        for (OrderItem item : order.getOrderItems()) {
            OrderStatus previousStatus = item.getStatus();

            // Nếu item đã thanh toán online → cần hoàn tiền
            if (previousStatus == OrderStatus.PAID && order.getPaymentMethod() != PaymentMethod.COD) {
                try {
                    item.setRefundStatus(RefundStatus.PENDING);
                    hasRefund = true;
                } catch (Exception e) {
                    item.setRefundStatus(RefundStatus.FAILED);
                    refundFailed = true;
                }
            }

            item.setStatus(OrderStatus.CANCELLED);
            item.setCancellationReason(dto.getCancellationReason());
            orderItemRepository.save(item);

            // Lưu lịch sử
            OrderHistory history = OrderHistory.builder()
                    .orderItem(item)
                    .previousStatus(previousStatus)
                    .newStatus(OrderStatus.CANCELLED)
                    .changeDate(new Date())
                    .build();
            orderHistoryRepository.save(history);

            // 5. Hoàn lại tồn kho và cập nhật Product cha
            if (item.getProductVariant() != null) {
                ProductVariant variant = item.getProductVariant();
                variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity().intValue());
                productVariantRepository.save(variant);

                Product parentProduct = variant.getProduct();
                if (parentProduct != null && parentProduct.getStatus() == ProductStatus.OUT_OF_STOCK) {
                    parentProduct.setStatus(ProductStatus.ACTIVE);
                }
            }
        }

        String message = "Hủy đơn hàng thành công!";

        // Gửi thông báo
        notificationService.createNotification(
                order.getUser().getId(),
                "Đơn hàng đã được hủy",
                "Đơn hàng #" + order.getId() + " đã được hủy thành công. Lý do: " + dto.getCancellationReason(),
                "WARNING",
                order.getId());

        if (hasRefund && !refundFailed) {
            message += " Yêu cầu hoàn tiền đang được xử lý.";
        } else if (refundFailed) {
            message += " Lỗi khi hoàn tiền một số sản phẩm, vui lòng liên hệ hỗ trợ.";
        }

        return MessageResponseDTO.builder()
                .message(message)
                .build();
    }

    @Override
    @Transactional
    public String retryPayment(Long userId, Long orderId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDashboardSummaryDTO getDashboardSummary(Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

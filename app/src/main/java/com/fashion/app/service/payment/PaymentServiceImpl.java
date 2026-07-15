package com.fashion.app.service.payment;

import com.fashion.app.dto.request.ProcessPaymentRequestDTO;
import com.fashion.app.dto.response.PaymentResponseDTO;
import com.fashion.app.model.Order;
import com.fashion.app.model.OrderHistory;
import com.fashion.app.model.OrderItem;
import com.fashion.app.model.PaymentTransaction;
import com.fashion.app.model.Product;
import com.fashion.app.model.ProductVariant;
import com.fashion.app.model.enums.OrderStatus;
import com.fashion.app.model.enums.PaymentProvider;
import com.fashion.app.model.enums.PaymentTransactionStatus;
import com.fashion.app.model.enums.ProductStatus;
import com.fashion.app.model.enums.RefundStatus;
import com.fashion.app.model.enums.Role;
import com.fashion.app.repository.OrderHistoryRepository;
import com.fashion.app.repository.OrderItemRepository;
import com.fashion.app.repository.OrderRepository;
import com.fashion.app.repository.PaymentTransactionRepository;
import com.fashion.app.repository.ProductVariantRepository;
import com.fashion.app.repository.UserRepository;
import com.fashion.app.service.notification.NotificationService;
import com.fashion.app.service.order.OrderManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fashion.app.exception.BadRequestException;
import com.fashion.app.model.enums.PaymentMethod;
import com.fashion.app.util.SecurityUtils;

import com.fashion.app.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final Set<OrderStatus> LATE_RECOVERABLE_STATUSES = Set.of(
            OrderStatus.PAYMENT_EXPIRED, OrderStatus.CANCELLED);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final MomoService momoService;
    private final OrderManagementService orderManagementService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void processMomoIPN(Map<String, Object> payload) {
        Map<String, String> stringParams = new java.util.HashMap<>();
        payload.forEach((k, v) -> stringParams.put(k, String.valueOf(v)));

        if (!momoService.verifySignature(stringParams)) {
            throw new RuntimeException("Chữ ký MoMo không hợp lệ!");
        }

        int resultCode = Integer.parseInt(stringParams.get("resultCode"));
        Long orderId = Long.parseLong(stringParams.get("orderId"));
        String transId = stringParams.get("transId");
        String requestId = stringParams.get("requestId");

        applyGatewayResult(orderId, transId, requestId, resultCode == 0, "IPN: " + payload);
    }

    @Override
    @Transactional
    public String processMomoReturn(Map<String, String> allParams) {
        if (!momoService.verifySignature(allParams)) {
            return "failed";
        }

        String resultCode = allParams.get("resultCode");
        Long orderId = Long.parseLong(allParams.get("orderId"));
        String transId = allParams.get("transId");
        String requestId = allParams.get("requestId");

        boolean success = "0".equals(resultCode);
        applyGatewayResult(orderId, transId, requestId, success, "RETURN: " + allParams);

        return success ? "success" : "failed";
    }

    /**
     * Điểm hội tụ xử lý kết quả từ cổng MoMo (IPN, return URL, hoặc query đối soát chủ động).
     * Bảo đảm idempotency qua transId trước khi cập nhật trạng thái đơn hàng.
     */
    private void applyGatewayResult(Long orderId, String transId, String requestId, boolean success, String rawPayload) {
        // 1. Chốt chặn idempotency: transId này đã được xử lý rồi thì bỏ qua, không làm lại (chống retry trùng của MoMo)
        if (transId != null && paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, transId)) {
            log.info("Bỏ qua vì transId {} của MoMo đã được xử lý trước đó (idempotent no-op) cho đơn #{}", transId, orderId);
            return;
        }

        // 2. Ghi nhận/khớp lại bản ghi PaymentTransaction tương ứng
        Optional<PaymentTransaction> pendingTx = requestId != null
                ? paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.MOMO, requestId)
                : Optional.empty();

        PaymentTransaction tx = pendingTx.orElseGet(() -> {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại!"));
            return PaymentTransaction.builder()
                    .order(order)
                    .provider(PaymentProvider.MOMO)
                    .requestId(requestId)
                    .amount(order.getTotalAmount())
                    .createdAt(Instant.now())
                    .build();
        });

        tx.setTransId(transId);
        tx.setStatus(success ? PaymentTransactionStatus.SUCCESS : PaymentTransactionStatus.FAILED);
        tx.setRawResponsePayload(rawPayload);
        tx.setProcessedAt(Instant.now());

        try {
            paymentTransactionRepository.saveAndFlush(tx);
        } catch (DataIntegrityViolationException e) {
            // Race: 2 IPN cho cùng transId gần như đồng thời, ràng buộc unique ở DB chặn lại -> coi như đã xử lý
            log.warn("Phát hiện xử lý trùng transId {} cho đơn #{} (race condition), bỏ qua.", transId, orderId);
            return;
        }

        updateOrderPayStatus(orderId, success);
    }

    private void updateOrderPayStatus(Long orderId, boolean success) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return;
        }
        Order order = orderOpt.get();

        boolean isLateRecovery = success && order.getStatus() != null && LATE_RECOVERABLE_STATUSES.contains(order.getStatus());

        if (isLateRecovery) {
            handleLatePaymentAfterExpiry(order);
            return;
        }

        for (OrderItem item : order.getOrderItems()) {
            if (item.getStatus() == OrderStatus.PENDING_PAYMENT || item.getStatus() == OrderStatus.PAYMENT_FAILED) {
                OrderStatus previousStatus = item.getStatus();
                OrderStatus newStatus = success ? OrderStatus.PAID : OrderStatus.PAYMENT_FAILED;

                item.setStatus(newStatus);
                orderItemRepository.save(item);

                // Lưu lịch sử nếu thành công
                if (success) {
                    OrderHistory history = OrderHistory.builder()
                            .orderItem(item)
                            .previousStatus(previousStatus)
                            .newStatus(newStatus)
                            .changeDate(new Date())
                            .build();
                    orderHistoryRepository.save(history);
                }
            }
        }
        // ĐỒNG BỘ TRẠNG THÁI TỔNG QUÁT CỦA ORDER
        orderManagementService.updateOverallOrderStatus(order);
    }

    /**
     * Case "tiền đã trừ nhưng mất kết nối": MoMo báo thanh toán thành công NHƯNG đơn đã bị
     * OrderExpirationTask tự động hủy/hoàn kho trước đó (do quá 10 phút không nhận được IPN kịp thời).
     * Không được im lặng bỏ qua giao dịch đã thu tiền thật — phải cố gắng khôi phục đơn và luôn báo admin.
     */
    private void handleLatePaymentAfterExpiry(Order order) {
        log.warn("Nhận thanh toán MoMo THÀNH CÔNG cho đơn #{} nhưng đơn đã ở trạng thái {} (đã hết hạn/hủy trước đó). Tiến hành xử lý bù trừ.",
                order.getId(), order.getStatus());

        boolean stockRestored = true;
        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            if (variant == null) continue;
            if (variant.getStockQuantity() < item.getQuantity()) {
                stockRestored = false;
                break;
            }
        }

        String adminMessage;
        if (stockRestored) {
            // Đủ hàng để bù lại: trừ kho lần nữa và đánh dấu đơn PAID
            for (OrderItem item : order.getOrderItems()) {
                OrderStatus previousStatus = item.getStatus();
                item.setStatus(OrderStatus.PAID);
                orderItemRepository.save(item);

                orderHistoryRepository.save(OrderHistory.builder()
                        .orderItem(item)
                        .previousStatus(previousStatus)
                        .newStatus(OrderStatus.PAID)
                        .changeDate(new Date())
                        .build());

                ProductVariant variant = item.getProductVariant();
                if (variant != null) {
                    variant.setStockQuantity(variant.getStockQuantity() - item.getQuantity());
                    productVariantRepository.save(variant);

                    Product parentProduct = variant.getProduct();
                    if (parentProduct != null && variant.getStockQuantity() <= 0
                            && parentProduct.getStatus() == ProductStatus.ACTIVE) {
                        parentProduct.setStatus(ProductStatus.OUT_OF_STOCK);
                    }
                }
            }
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);

            adminMessage = "Đơn hàng #" + order.getId()
                    + " đã tự động khôi phục về trạng thái PAID sau khi MoMo báo thanh toán thành công muộn (đơn từng bị hết hạn). "
                    + "Vui lòng kiểm tra lại tồn kho và xử lý đơn như bình thường.";
        } else {
            // Không đủ hàng để giao (đã bán mất trong lúc chờ): KHÔNG đánh dấu PAID, đánh dấu cần hoàn tiền khẩn cấp
            for (OrderItem item : order.getOrderItems()) {
                item.setRefundStatus(RefundStatus.PENDING);
                orderItemRepository.save(item);
            }

            adminMessage = "CẢNH BÁO: Đơn hàng #" + order.getId()
                    + " đã được khách thanh toán MoMo thành công (muộn) nhưng tồn kho không còn đủ để giao "
                    + "(đơn đã bị hủy/hết hạn trước đó và sản phẩm có thể đã bán cho khách khác). "
                    + "Cần XỬ LÝ HOÀN TIỀN THỦ CÔNG cho khách ngay.";
        }

        notifyAllAdmins(
                stockRestored ? "Khôi phục đơn hàng thanh toán muộn" : "Cần hoàn tiền khẩn cấp - đơn thanh toán muộn",
                adminMessage,
                stockRestored ? "WARNING" : "ERROR",
                order.getId());
    }

    private void notifyAllAdmins(String title, String content, String type, Long relatedOrderId) {
        userRepository.findByRole(Role.ADMIN, Pageable.unpaged())
                .forEach(admin -> notificationService.createNotification(
                        admin.getId(), title, content, type, relatedOrderId));
    }

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(ProcessPaymentRequestDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));
        // Giữ lại logic cũ (mock) nếu cần, hoặc refactor để dùng chung updateOrderPayStatus
        updateOrderPayStatus(order.getId(), true);
        return PaymentResponseDTO.builder()
                .status("SUCCESS")
                .message("Thanh toán thành công (Mock)!")
                .build();
    }

    @Override
    @Transactional
    public PaymentResponseDTO recreateMomoPayment(Long orderId) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại!"));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập đơn hàng này!");
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT || order.getPaymentMethod() != PaymentMethod.MOMO) {
            throw new BadRequestException("Đơn hàng không đủ điều kiện để thanh toán lại");
        }

        String paymentUrl = momoService.createPaymentUrl(order.getId(), order.getTotalAmount());

        return PaymentResponseDTO.builder()
                .status("SUCCESS")
                .message("Tạo mới liên kết thanh toán thành công")
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional
    public boolean reconcilePendingMomoPayment(Long orderId) {
        Optional<PaymentTransaction> pendingTx = paymentTransactionRepository
                .findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(orderId, PaymentProvider.MOMO, PaymentTransactionStatus.PENDING);

        if (pendingTx.isEmpty() || pendingTx.get().getRequestId() == null) {
            log.info("Không có giao dịch MoMo PENDING nào để đối soát cho đơn #{}", orderId);
            return false;
        }

        String requestId = pendingTx.get().getRequestId();
        MomoService.MomoQueryResult result = momoService.queryTransaction(orderId, requestId);

        if (!result.isSuccess()) {
            log.info("Đối soát MoMo cho đơn #{} (requestId={}): chưa xác nhận thành công (resultCode={}, message={})",
                    orderId, requestId, result.resultCode(), result.message());
            return false;
        }

        log.warn("Đối soát MoMo phát hiện đơn #{} (requestId={}) ĐÃ thanh toán thành công (transId={}) — khôi phục đơn thay vì để hết hạn.",
                orderId, requestId, result.transId());

        applyGatewayResult(orderId, result.transId(), requestId, true, "RECONCILE_QUERY: " + result.rawResponse());
        return true;
    }
}

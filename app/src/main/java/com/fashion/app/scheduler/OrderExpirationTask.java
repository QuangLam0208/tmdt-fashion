package com.fashion.app.scheduler;

import com.fashion.app.model.Order;
import com.fashion.app.model.OrderItem;
import com.fashion.app.model.enums.OrderStatus;
import com.fashion.app.model.enums.PaymentMethod;
import com.fashion.app.repository.OrderRepository;
import com.fashion.app.repository.UserCouponRepository;
import com.fashion.app.service.order.OrderService;
import com.fashion.app.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpirationTask {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserCouponRepository userCouponRepository;
    private final PaymentService paymentService;

    /**
     * Chạy mỗi phút để quét các đơn hàng PENDING_PAYMENT quá 10 phút.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanupExpiredOrders() {
        Instant tenMinutesAgo = Instant.now().minus(Duration.ofMinutes(10));
        List<Order> expiredOrders = orderRepository.findByStatusAndOrderDateBefore(OrderStatus.PENDING_PAYMENT, tenMinutesAgo);

        if (expiredOrders.isEmpty()) {
            return;
        }

        log.info("Phát hiện {} đơn hàng quá hạn thanh toán. Đang tiến hành hủy...", expiredOrders.size());

        for (Order order : expiredOrders) {
            // Trước khi hủy, nếu đơn thanh toán qua MoMo thì chủ động tra cứu lại với cổng thanh toán
            // để tránh case tiền đã trừ thành công nhưng IPN bị mất do mất kết nối (không hủy nhầm đơn đã trả tiền).
            if (order.getPaymentMethod() == PaymentMethod.MOMO) {
                boolean reconciledAsPaid = paymentService.reconcilePendingMomoPayment(order.getId());
                if (reconciledAsPaid) {
                    log.warn("Đơn hàng #{} thực ra ĐÃ thanh toán MoMo thành công (phát hiện qua đối soát) — bỏ qua việc hủy do hết hạn.", order.getId());
                    continue;
                }
            }

            log.info("Hủy đơn hàng #{} do quá hạn thanh toán 10 phút.", order.getId());

            // 1. Cập nhật trạng thái đơn hàng sang PAYMENT_EXPIRED
            order.setStatus(OrderStatus.PAYMENT_EXPIRED);

            // 2. Cập nhật trạng thái của từng món đồ
            for (OrderItem item : order.getOrderItems()) {
                item.setStatus(OrderStatus.PAYMENT_EXPIRED);
            }

            // 3. Hoàn kho
            orderService.revertInventory(order.getId());

            // 4. Hoàn trạng thái Coupon (nếu có)
            if (order.getCoupon() != null) {
                userCouponRepository.findByUserIdAndCouponCode(order.getUser().getId(), order.getCoupon().getCode())
                        .ifPresent(uc -> {
                            uc.setUsed(false);
                            userCouponRepository.save(uc);
                            log.info("Đã hoàn lại Coupon [{}] cho user [{}]", order.getCoupon().getCode(), order.getUser().getId());
                        });
            }

            orderRepository.save(order);
        }
    }
}

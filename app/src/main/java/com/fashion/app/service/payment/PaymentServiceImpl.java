package com.fashion.app.service.payment;

import com.fashion.app.dto.request.ProcessPaymentRequestDTO;
import com.fashion.app.dto.response.PaymentResponseDTO;
import com.fashion.app.model.Order;
import com.fashion.app.model.OrderHistory;
import com.fashion.app.model.OrderItem;
import com.fashion.app.model.enums.OrderStatus;
import com.fashion.app.repository.OrderHistoryRepository;
import com.fashion.app.repository.OrderItemRepository;
import com.fashion.app.repository.OrderRepository;
import com.fashion.app.service.order.OrderManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final MomoService momoService;
    private final OrderManagementService orderManagementService;

    @Override
    @Transactional
    public void processMomoIPN(Map<String, Object> payload) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public String processMomoReturn(Map<String, String> allParams) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void updateOrderPayStatus(Long orderId, boolean success) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
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
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

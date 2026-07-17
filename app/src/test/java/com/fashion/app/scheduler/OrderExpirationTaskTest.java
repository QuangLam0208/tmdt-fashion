package com.fashion.app.scheduler;

import com.fashion.app.model.Order;
import com.fashion.app.model.OrderItem;
import com.fashion.app.model.enums.OrderStatus;
import com.fashion.app.model.enums.PaymentMethod;
import com.fashion.app.repository.OrderRepository;
import com.fashion.app.repository.UserCouponRepository;
import com.fashion.app.service.order.OrderService;
import com.fashion.app.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderExpirationTaskTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderExpirationTask orderExpirationTask;

    private Order vnPayOrder;
    private OrderItem vnPayOrderItem;

    @BeforeEach
    void setUp() {
        vnPayOrder = new Order();
        vnPayOrder.setId(1L);
        vnPayOrder.setPaymentMethod(PaymentMethod.VNPAY);
        vnPayOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        vnPayOrderItem = new OrderItem();
        vnPayOrderItem.setId(10L);
        vnPayOrderItem.setStatus(OrderStatus.PENDING_PAYMENT);
        vnPayOrder.setOrderItems(List.of(vnPayOrderItem));
    }

    @Test
    @DisplayName("Case: tiền đã trừ nhưng mất kết nối — đối soát VNPay xác nhận đã thanh toán thì KHÔNG hủy đơn")
    void cleanupExpiredOrders_VNPayOrderReconciledAsPaid_SkipsExpiration() {
        when(orderRepository.findByStatusAndOrderDateBefore(eq(OrderStatus.PENDING_PAYMENT), any(Instant.class)))
                .thenReturn(List.of(vnPayOrder));
        when(paymentService.reconcilePendingVNPayPayment(1L)).thenReturn(true);

        orderExpirationTask.cleanupExpiredOrders();

        verify(paymentService, times(1)).reconcilePendingVNPayPayment(1L);
        // Không được hủy: không gọi revertInventory, không lưu order với status hết hạn
        verify(orderService, never()).revertInventory(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Đơn VNPay đối soát vẫn CHƯA thanh toán thì tiếp tục hủy như bình thường")
    void cleanupExpiredOrders_VNPayOrderNotReconciled_ProceedsToExpire() {
        when(orderRepository.findByStatusAndOrderDateBefore(eq(OrderStatus.PENDING_PAYMENT), any(Instant.class)))
                .thenReturn(List.of(vnPayOrder));
        when(paymentService.reconcilePendingVNPayPayment(1L)).thenReturn(false);

        orderExpirationTask.cleanupExpiredOrders();

        assertEquals(OrderStatus.PAYMENT_EXPIRED, vnPayOrder.getStatus());
        assertEquals(OrderStatus.PAYMENT_EXPIRED, vnPayOrderItem.getStatus());
        verify(orderService, times(1)).revertInventory(1L);
        verify(orderRepository, times(1)).save(vnPayOrder);
    }

    @Test
    @DisplayName("Đơn COD quá hạn không cần đối soát VNPay, hủy trực tiếp")
    void cleanupExpiredOrders_CodOrder_ExpiresWithoutReconciliation() {
        Order codOrder = new Order();
        codOrder.setId(2L);
        codOrder.setPaymentMethod(PaymentMethod.COD);
        codOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        OrderItem codItem = new OrderItem();
        codItem.setStatus(OrderStatus.PENDING_PAYMENT);
        codOrder.setOrderItems(List.of(codItem));

        when(orderRepository.findByStatusAndOrderDateBefore(eq(OrderStatus.PENDING_PAYMENT), any(Instant.class)))
                .thenReturn(List.of(codOrder));

        orderExpirationTask.cleanupExpiredOrders();

        verify(paymentService, never()).reconcilePendingVNPayPayment(anyLong());
        assertEquals(OrderStatus.PAYMENT_EXPIRED, codOrder.getStatus());
        verify(orderService, times(1)).revertInventory(2L);
    }
}

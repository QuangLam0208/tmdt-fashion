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

import java.util.Date;
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

    private Order momoOrder;
    private OrderItem momoOrderItem;

    @BeforeEach
    void setUp() {
        momoOrder = new Order();
        momoOrder.setId(1L);
        momoOrder.setPaymentMethod(PaymentMethod.MOMO);
        momoOrder.setStatus(OrderStatus.PENDING_PAYMENT);

        momoOrderItem = new OrderItem();
        momoOrderItem.setId(10L);
        momoOrderItem.setStatus(OrderStatus.PENDING_PAYMENT);
        momoOrder.setOrderItems(List.of(momoOrderItem));
    }

    @Test
    @DisplayName("Case: tiền đã trừ nhưng mất kết nối — đối soát MoMo xác nhận đã thanh toán thì KHÔNG hủy đơn")
    void cleanupExpiredOrders_MomoOrderReconciledAsPaid_SkipsExpiration() {
        when(orderRepository.findByStatusAndOrderDateBefore(eq(OrderStatus.PENDING_PAYMENT), any(Date.class).toInstant()))
                .thenReturn(List.of(momoOrder));
        when(paymentService.reconcilePendingMomoPayment(1L)).thenReturn(true);

        orderExpirationTask.cleanupExpiredOrders();

        verify(paymentService, times(1)).reconcilePendingMomoPayment(1L);
        // Không được hủy: không gọi revertInventory, không lưu order với status hết hạn
        verify(orderService, never()).revertInventory(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Đơn MoMo đối soát vẫn CHƯA thanh toán thì tiếp tục hủy như bình thường")
    void cleanupExpiredOrders_MomoOrderNotReconciled_ProceedsToExpire() {
        when(orderRepository.findByStatusAndOrderDateBefore(eq(OrderStatus.PENDING_PAYMENT), any(Date.class).toInstant()))
                .thenReturn(List.of(momoOrder));
        when(paymentService.reconcilePendingMomoPayment(1L)).thenReturn(false);

        orderExpirationTask.cleanupExpiredOrders();

        assertEquals(OrderStatus.PAYMENT_EXPIRED, momoOrder.getStatus());
        assertEquals(OrderStatus.PAYMENT_EXPIRED, momoOrderItem.getStatus());
        verify(orderService, times(1)).revertInventory(1L);
        verify(orderRepository, times(1)).save(momoOrder);
    }

    @Test
    @DisplayName("Đơn COD quá hạn không cần đối soát MoMo, hủy trực tiếp")
    void cleanupExpiredOrders_CodOrder_ExpiresWithoutReconciliation() {
        Order codOrder = new Order();
        codOrder.setId(2L);
        codOrder.setPaymentMethod(PaymentMethod.COD);
        codOrder.setStatus(OrderStatus.PENDING_PAYMENT);
        OrderItem codItem = new OrderItem();
        codItem.setStatus(OrderStatus.PENDING_PAYMENT);
        codOrder.setOrderItems(List.of(codItem));

        when(orderRepository.findByStatusAndOrderDateBefore(eq(OrderStatus.PENDING_PAYMENT), any(Date.class).toInstant()))
                .thenReturn(List.of(codOrder));

        orderExpirationTask.cleanupExpiredOrders();

        verify(paymentService, never()).reconcilePendingMomoPayment(anyLong());
        assertEquals(OrderStatus.PAYMENT_EXPIRED, codOrder.getStatus());
        verify(orderService, times(1)).revertInventory(2L);
    }
}

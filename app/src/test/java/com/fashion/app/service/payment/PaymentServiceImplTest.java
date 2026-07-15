package com.fashion.app.service.payment;

import com.fashion.app.model.Order;
import com.fashion.app.model.OrderItem;
import com.fashion.app.model.PaymentTransaction;
import com.fashion.app.model.ProductVariant;
import com.fashion.app.model.User;
import com.fashion.app.model.enums.OrderStatus;
import com.fashion.app.model.enums.PaymentMethod;
import com.fashion.app.model.enums.PaymentProvider;
import com.fashion.app.model.enums.PaymentTransactionStatus;
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
import com.fashion.app.util.SecurityUtils;
import com.fashion.app.exception.BadRequestException;
import com.fashion.app.exception.ResourceNotFoundException;
import com.fashion.app.dto.response.PaymentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderHistoryRepository orderHistoryRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MomoService momoService;

    @Mock
    private OrderManagementService orderManagementService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order mockOrder;
    private OrderItem mockOrderItem;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setId(1L);

        mockOrderItem = new OrderItem();
        mockOrderItem.setId(10L);
        mockOrderItem.setStatus(OrderStatus.PENDING_PAYMENT);
        mockOrderItem.setOrder(mockOrder);

        mockOrder.setOrderItems(List.of(mockOrderItem));
    }

    @Test
    void processMomoIPN_SignatureInvalid_ThrowsException() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", 1L);
        payload.put("resultCode", 0);

        when(momoService.verifySignature(anyMap())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> paymentService.processMomoIPN(payload));
        assertEquals("Chữ ký MoMo không hợp lệ!", exception.getMessage());

        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    void processMomoIPN_SignatureValid_Success_UpdatesStatusToPaid() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", 1L);
        payload.put("resultCode", 0);
        payload.put("transId", "trans-001");
        payload.put("requestId", "req-001");

        when(momoService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, "trans-001")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.MOMO, "req-001")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        paymentService.processMomoIPN(payload);

        assertEquals(OrderStatus.PAID, mockOrderItem.getStatus());
        verify(orderItemRepository, times(1)).save(mockOrderItem);
        verify(orderHistoryRepository, times(1)).save(any());
        verify(orderManagementService, times(1)).updateOverallOrderStatus(mockOrder);
        verify(paymentTransactionRepository, times(1)).saveAndFlush(any(PaymentTransaction.class));
    }

    @Test
    void processMomoIPN_SignatureValid_Failed_UpdatesStatusToPaymentFailed() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", 1L);
        payload.put("resultCode", 1006);
        payload.put("transId", "trans-002");
        payload.put("requestId", "req-002");

        when(momoService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, "trans-002")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.MOMO, "req-002")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        paymentService.processMomoIPN(payload);

        assertEquals(OrderStatus.PAYMENT_FAILED, mockOrderItem.getStatus());
        verify(orderItemRepository, times(1)).save(mockOrderItem);
        verify(orderHistoryRepository, never()).save(any());
        verify(orderManagementService, times(1)).updateOverallOrderStatus(mockOrder);
    }

    @Test
    void processMomoIPN_DuplicateTransId_IsIdempotent_DoesNotReprocess() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", 1L);
        payload.put("resultCode", 0);
        payload.put("transId", "trans-003");
        payload.put("requestId", "req-003");

        when(momoService.verifySignature(anyMap())).thenReturn(true);
        // Giả lập MoMo gọi lại IPN cho transId đã xử lý trước đó (retry chuẩn của cổng thanh toán)
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, "trans-003")).thenReturn(true);

        paymentService.processMomoIPN(payload);

        // Không được xử lý lại: không tìm Order, không lưu trạng thái, không đồng bộ order
        verify(orderRepository, never()).findById(anyLong());
        verify(orderItemRepository, never()).save(any());
        verify(paymentTransactionRepository, never()).saveAndFlush(any());
        verify(orderManagementService, never()).updateOverallOrderStatus(any());
    }

    @Test
    void processMomoReturn_SignatureInvalid_ReturnsFailed() {
        Map<String, String> payload = new HashMap<>();
        payload.put("orderId", "1");
        payload.put("resultCode", "0");

        when(momoService.verifySignature(anyMap())).thenReturn(false);

        String result = paymentService.processMomoReturn(payload);

        assertEquals("failed", result);
        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    void processMomoReturn_SignatureValid_Success_ReturnsSuccess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("orderId", "1");
        payload.put("resultCode", "0");
        payload.put("transId", "trans-004");
        payload.put("requestId", "req-004");

        when(momoService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, "trans-004")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.MOMO, "req-004")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        String result = paymentService.processMomoReturn(payload);

        assertEquals("success", result);
        assertEquals(OrderStatus.PAID, mockOrderItem.getStatus());
        verify(orderItemRepository, times(1)).save(mockOrderItem);
    }

    /**
     * Case "tiền đã trừ nhưng mất kết nối": IPN báo thành công đến MUỘN, sau khi đơn đã bị
     * OrderExpirationTask tự động chuyển sang PAYMENT_EXPIRED. Hệ thống phải tự khôi phục đơn
     * (nếu còn đủ tồn kho) thay vì im lặng bỏ qua, và bắt buộc cảnh báo admin.
     */
    @Test
    void processMomoIPN_LatePaymentAfterExpiry_WithStock_RecoversOrderAndNotifiesAdmin() {
        ProductVariant variant = ProductVariant.builder().id(500L).stockQuantity(5L).build();
        mockOrderItem.setStatus(OrderStatus.PAYMENT_EXPIRED);
        mockOrderItem.setQuantity(1L);
        mockOrderItem.setProductVariant(variant);
        mockOrder.setStatus(OrderStatus.PAYMENT_EXPIRED);

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", 1L);
        payload.put("resultCode", 0);
        payload.put("transId", "trans-late-001");
        payload.put("requestId", "req-late-001");

        User admin = User.builder().id(999L).role(Role.ADMIN).build();

        when(momoService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, "trans-late-001")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.MOMO, "req-late-001")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(userRepository.findByRole(eq(Role.ADMIN), any())).thenReturn(new PageImpl<>(List.of(admin)));

        paymentService.processMomoIPN(payload);

        assertEquals(OrderStatus.PAID, mockOrderItem.getStatus());
        assertEquals(OrderStatus.PAID, mockOrder.getStatus());
        assertEquals(4L, variant.getStockQuantity(), "Phải trừ lại kho vì đơn được khôi phục thành PAID");
        verify(productVariantRepository, times(1)).save(variant);
        verify(notificationService, times(1)).createNotification(
                eq(999L), anyString(), anyString(), eq("WARNING"), eq(1L));
    }

    @Test
    void processMomoIPN_LatePaymentAfterExpiry_OutOfStock_FlagsUrgentRefundAndAlertsAdmin() {
        ProductVariant variant = ProductVariant.builder().id(501L).stockQuantity(0L).build();
        mockOrderItem.setStatus(OrderStatus.PAYMENT_EXPIRED);
        mockOrderItem.setQuantity(1L);
        mockOrderItem.setProductVariant(variant);
        mockOrder.setStatus(OrderStatus.CANCELLED);

        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", 1L);
        payload.put("resultCode", 0);
        payload.put("transId", "trans-late-002");
        payload.put("requestId", "req-late-002");

        User admin = User.builder().id(999L).role(Role.ADMIN).build();

        when(momoService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, "trans-late-002")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.MOMO, "req-late-002")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(userRepository.findByRole(eq(Role.ADMIN), any())).thenReturn(new PageImpl<>(List.of(admin)));

        paymentService.processMomoIPN(payload);

        // Không đủ hàng: KHÔNG được tự ý đánh dấu PAID, phải để hoàn tiền thủ công
        assertEquals(OrderStatus.PAYMENT_EXPIRED, mockOrderItem.getStatus());
        assertEquals(RefundStatus.PENDING, mockOrderItem.getRefundStatus());
        verify(productVariantRepository, never()).save(any());
        verify(notificationService, times(1)).createNotification(
                eq(999L), anyString(), anyString(), eq("ERROR"), eq(1L));
    }

    @Test
    void recreateMomoPayment_OrderNotFound_ThrowsException() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(1L);
            when(orderRepository.findById(1L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.recreateMomoPayment(1L));
            assertEquals("Đơn hàng không tồn tại!", exception.getMessage());
        }
    }

    @Test
    void recreateMomoPayment_NotOwner_ThrowsException() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(2L);
            User user = new User();
            user.setId(1L);
            mockOrder.setUser(user);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

            Exception exception = assertThrows(AccessDeniedException.class, () -> paymentService.recreateMomoPayment(1L));
            assertEquals("Bạn không có quyền truy cập đơn hàng này!", exception.getMessage());
        }
    }

    @Test
    void recreateMomoPayment_InvalidStatus_ThrowsException() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(1L);
            User user = new User();
            user.setId(1L);
            mockOrder.setUser(user);
            mockOrder.setStatus(OrderStatus.PAID);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

            Exception exception = assertThrows(BadRequestException.class, () -> paymentService.recreateMomoPayment(1L));
            assertEquals("Đơn hàng không đủ điều kiện để thanh toán lại", exception.getMessage());
        }
    }

    @Test
    void recreateMomoPayment_Success_ReturnsPaymentUrl() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(1L);
            User user = new User();
            user.setId(1L);
            mockOrder.setUser(user);
            mockOrder.setStatus(OrderStatus.PENDING_PAYMENT);
            mockOrder.setPaymentMethod(PaymentMethod.MOMO);
            mockOrder.setTotalAmount(100000.0);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
            when(momoService.createPaymentUrl(1L, 100000.0)).thenReturn("http://mock-payment-url");

            PaymentResponseDTO result = paymentService.recreateMomoPayment(1L);

            assertNotNull(result);
            assertEquals("SUCCESS", result.getStatus());
            assertEquals("http://mock-payment-url", result.getPaymentUrl());
            assertEquals("Tạo mới liên kết thanh toán thành công", result.getMessage());
        }
    }

    /**
     * Đối soát chủ động (dùng bởi scheduler trước khi hủy đơn quá hạn): nếu MoMo xác nhận đã
     * thanh toán thành công thì phải cập nhật đơn ngay, tránh hủy nhầm đơn đã thu tiền.
     */
    @Test
    void reconcilePendingMomoPayment_GatewayConfirmsSuccess_UpdatesOrderAndReturnsTrue() {
        PaymentTransaction pendingTx = PaymentTransaction.builder()
                .order(mockOrder)
                .provider(PaymentProvider.MOMO)
                .requestId("req-reconcile-001")
                .amount(100000.0)
                .status(PaymentTransactionStatus.PENDING)
                .build();

        when(paymentTransactionRepository.findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(
                1L, PaymentProvider.MOMO, PaymentTransactionStatus.PENDING)).thenReturn(Optional.of(pendingTx));
        when(momoService.queryTransaction(1L, "req-reconcile-001"))
                .thenReturn(new MomoService.MomoQueryResult(0, "trans-reconcile-001", "Success", "{}"));
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.MOMO, "trans-reconcile-001")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.MOMO, "req-reconcile-001")).thenReturn(Optional.of(pendingTx));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        boolean result = paymentService.reconcilePendingMomoPayment(1L);

        assertTrue(result);
        assertEquals(OrderStatus.PAID, mockOrderItem.getStatus());
    }

    @Test
    void reconcilePendingMomoPayment_GatewayNotYetSuccess_ReturnsFalse() {
        PaymentTransaction pendingTx = PaymentTransaction.builder()
                .order(mockOrder)
                .provider(PaymentProvider.MOMO)
                .requestId("req-reconcile-002")
                .amount(100000.0)
                .status(PaymentTransactionStatus.PENDING)
                .build();

        when(paymentTransactionRepository.findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(
                1L, PaymentProvider.MOMO, PaymentTransactionStatus.PENDING)).thenReturn(Optional.of(pendingTx));
        when(momoService.queryTransaction(1L, "req-reconcile-002"))
                .thenReturn(new MomoService.MomoQueryResult(1006, null, "Transaction not found", "{}"));

        boolean result = paymentService.reconcilePendingMomoPayment(1L);

        assertFalse(result);
        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    void reconcilePendingMomoPayment_NoPendingTransaction_ReturnsFalse() {
        when(paymentTransactionRepository.findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(
                1L, PaymentProvider.MOMO, PaymentTransactionStatus.PENDING)).thenReturn(Optional.empty());

        boolean result = paymentService.reconcilePendingMomoPayment(1L);

        assertFalse(result);
        verify(momoService, never()).queryTransaction(anyLong(), anyString());
    }
}

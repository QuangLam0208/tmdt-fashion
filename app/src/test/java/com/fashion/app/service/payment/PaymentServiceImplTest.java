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
    private VNPayService vnPayService;

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
    void processVNPayIPN_SignatureInvalid_ThrowsException() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "00");

        when(vnPayService.verifySignature(anyMap())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> paymentService.processVNPayIPN(payload));
        assertEquals("Chữ ký VNPay không hợp lệ!", exception.getMessage());

        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    void processVNPayIPN_SignatureValid_Success_UpdatesStatusToPaid() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "00");
        payload.put("vnp_TransactionNo", "trans-001");

        when(vnPayService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.VNPAY, "trans-001")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.VNPAY, "1_1000")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        paymentService.processVNPayIPN(payload);

        assertEquals(OrderStatus.PAID, mockOrderItem.getStatus());
        verify(orderItemRepository, times(1)).save(mockOrderItem);
        verify(orderHistoryRepository, times(1)).save(any());
        verify(orderManagementService, times(1)).updateOverallOrderStatus(mockOrder);
        verify(paymentTransactionRepository, times(1)).saveAndFlush(any(PaymentTransaction.class));
    }

    @Test
    void processVNPayIPN_SignatureValid_Failed_UpdatesStatusToPaymentFailed() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "24");
        payload.put("vnp_TransactionNo", "trans-002");

        when(vnPayService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.VNPAY, "trans-002")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.VNPAY, "1_1000")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        paymentService.processVNPayIPN(payload);

        assertEquals(OrderStatus.PAYMENT_FAILED, mockOrderItem.getStatus());
        verify(orderItemRepository, times(1)).save(mockOrderItem);
        verify(orderHistoryRepository, never()).save(any());
        verify(orderManagementService, times(1)).updateOverallOrderStatus(mockOrder);
    }

    @Test
    void processVNPayIPN_DuplicateTransId_IsIdempotent_DoesNotReprocess() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "00");
        payload.put("vnp_TransactionNo", "trans-003");

        when(vnPayService.verifySignature(anyMap())).thenReturn(true);
        // Giả lập VNPay gọi lại IPN cho transId đã xử lý trước đó (retry chuẩn của cổng thanh toán)
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.VNPAY, "trans-003")).thenReturn(true);

        paymentService.processVNPayIPN(payload);

        // Không được xử lý lại: không tìm Order, không lưu trạng thái, không đồng bộ order
        verify(orderRepository, never()).findById(anyLong());
        verify(orderItemRepository, never()).save(any());
        verify(paymentTransactionRepository, never()).saveAndFlush(any());
        verify(orderManagementService, never()).updateOverallOrderStatus(any());
    }

    @Test
    void processVNPayReturn_SignatureInvalid_ReturnsFailed() {
        Map<String, String> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "00");

        when(vnPayService.verifySignature(anyMap())).thenReturn(false);

        String result = paymentService.processVNPayReturn(payload);

        assertEquals("failed", result);
        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    void processVNPayReturn_SignatureValid_Success_ReturnsSuccess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "00");
        payload.put("vnp_TransactionNo", "trans-004");

        when(vnPayService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.VNPAY, "trans-004")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.VNPAY, "1_1000")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        String result = paymentService.processVNPayReturn(payload);

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
    void processVNPayIPN_LatePaymentAfterExpiry_WithStock_RecoversOrderAndNotifiesAdmin() {
        ProductVariant variant = ProductVariant.builder().id(500L).stockQuantity(5L).build();
        mockOrderItem.setStatus(OrderStatus.PAYMENT_EXPIRED);
        mockOrderItem.setQuantity(1L);
        mockOrderItem.setProductVariant(variant);
        mockOrder.setStatus(OrderStatus.PAYMENT_EXPIRED);

        Map<String, Object> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "00");
        payload.put("vnp_TransactionNo", "trans-late-001");

        User admin = User.builder().id(999L).role(Role.ADMIN).build();

        when(vnPayService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.VNPAY, "trans-late-001")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.VNPAY, "1_1000")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(userRepository.findByRole(eq(Role.ADMIN), any())).thenReturn(new PageImpl<>(List.of(admin)));

        paymentService.processVNPayIPN(payload);

        assertEquals(OrderStatus.PAID, mockOrderItem.getStatus());
        assertEquals(OrderStatus.PAID, mockOrder.getStatus());
        assertEquals(4L, variant.getStockQuantity(), "Phải trừ lại kho vì đơn được khôi phục thành PAID");
        verify(productVariantRepository, times(1)).save(variant);
        verify(notificationService, times(1)).createNotification(
                eq(999L), anyString(), anyString(), eq("WARNING"), eq(1L));
    }

    @Test
    void processVNPayIPN_LatePaymentAfterExpiry_OutOfStock_FlagsUrgentRefundAndAlertsAdmin() {
        ProductVariant variant = ProductVariant.builder().id(501L).stockQuantity(0L).build();
        mockOrderItem.setStatus(OrderStatus.PAYMENT_EXPIRED);
        mockOrderItem.setQuantity(1L);
        mockOrderItem.setProductVariant(variant);
        mockOrder.setStatus(OrderStatus.CANCELLED);

        Map<String, Object> payload = new HashMap<>();
        payload.put("vnp_TxnRef", "1_1000");
        payload.put("vnp_ResponseCode", "00");
        payload.put("vnp_TransactionNo", "trans-late-002");

        User admin = User.builder().id(999L).role(Role.ADMIN).build();

        when(vnPayService.verifySignature(anyMap())).thenReturn(true);
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.VNPAY, "trans-late-002")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.VNPAY, "1_1000")).thenReturn(Optional.empty());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(userRepository.findByRole(eq(Role.ADMIN), any())).thenReturn(new PageImpl<>(List.of(admin)));

        paymentService.processVNPayIPN(payload);

        // Không đủ hàng: KHÔNG được tự ý đánh dấu PAID, phải để hoàn tiền thủ công
        assertEquals(OrderStatus.PAYMENT_EXPIRED, mockOrderItem.getStatus());
        assertEquals(RefundStatus.PENDING, mockOrderItem.getRefundStatus());
        verify(productVariantRepository, never()).save(any());
        verify(notificationService, times(1)).createNotification(
                eq(999L), anyString(), anyString(), eq("ERROR"), eq(1L));
    }

    @Test
    void recreateVNPayPayment_OrderNotFound_ThrowsException() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(1L);
            when(orderRepository.findById(1L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.recreateVNPayPayment(1L));
            assertEquals("Đơn hàng không tồn tại!", exception.getMessage());
        }
    }

    @Test
    void recreateVNPayPayment_NotOwner_ThrowsException() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(2L);
            User user = new User();
            user.setId(1L);
            mockOrder.setUser(user);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

            Exception exception = assertThrows(AccessDeniedException.class, () -> paymentService.recreateVNPayPayment(1L));
            assertEquals("Bạn không có quyền truy cập đơn hàng này!", exception.getMessage());
        }
    }

    @Test
    void recreateVNPayPayment_InvalidStatus_ThrowsException() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(1L);
            User user = new User();
            user.setId(1L);
            mockOrder.setUser(user);
            mockOrder.setStatus(OrderStatus.PAID);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

            Exception exception = assertThrows(BadRequestException.class, () -> paymentService.recreateVNPayPayment(1L));
            assertEquals("Đơn hàng không đủ điều kiện để thanh toán lại", exception.getMessage());
        }
    }

    @Test
    void recreateVNPayPayment_Success_ReturnsPaymentUrl() {
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getAuthenticatedUserId).thenReturn(1L);
            User user = new User();
            user.setId(1L);
            mockOrder.setUser(user);
            mockOrder.setStatus(OrderStatus.PENDING_PAYMENT);
            mockOrder.setPaymentMethod(PaymentMethod.VNPAY);
            mockOrder.setTotalAmount(100000.0);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
            when(vnPayService.createPaymentUrl(1L, 100000.0)).thenReturn("http://mock-payment-url");

            PaymentResponseDTO result = paymentService.recreateVNPayPayment(1L);

            assertNotNull(result);
            assertEquals("SUCCESS", result.getStatus());
            assertEquals("http://mock-payment-url", result.getPaymentUrl());
            assertEquals("Tạo mới liên kết thanh toán thành công", result.getMessage());
        }
    }

    /**
     * Đối soát chủ động (dùng bởi scheduler trước khi hủy đơn quá hạn): nếu VNPay xác nhận đã
     * thanh toán thành công thì phải cập nhật đơn ngay, tránh hủy nhầm đơn đã thu tiền.
     */
    @Test
    void reconcilePendingVNPayPayment_GatewayConfirmsSuccess_UpdatesOrderAndReturnsTrue() {
        PaymentTransaction pendingTx = PaymentTransaction.builder()
                .order(mockOrder)
                .provider(PaymentProvider.VNPAY)
                .requestId("1_1000")
                .amount(100000.0)
                .status(PaymentTransactionStatus.PENDING)
                .build();

        when(paymentTransactionRepository.findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(
                1L, PaymentProvider.VNPAY, PaymentTransactionStatus.PENDING)).thenReturn(Optional.of(pendingTx));
        when(vnPayService.queryTransaction(1L, "1_1000"))
                .thenReturn(new VNPayService.VNPayQueryResult("00", "00", "trans-reconcile-001", "Success", "{}"));
        when(paymentTransactionRepository.existsByProviderAndTransId(PaymentProvider.VNPAY, "trans-reconcile-001")).thenReturn(false);
        when(paymentTransactionRepository.findByProviderAndRequestId(PaymentProvider.VNPAY, "1_1000")).thenReturn(Optional.of(pendingTx));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        boolean result = paymentService.reconcilePendingVNPayPayment(1L);

        assertTrue(result);
        assertEquals(OrderStatus.PAID, mockOrderItem.getStatus());
    }

    @Test
    void reconcilePendingVNPayPayment_GatewayNotYetSuccess_ReturnsFalse() {
        PaymentTransaction pendingTx = PaymentTransaction.builder()
                .order(mockOrder)
                .provider(PaymentProvider.VNPAY)
                .requestId("1_1001")
                .amount(100000.0)
                .status(PaymentTransactionStatus.PENDING)
                .build();

        when(paymentTransactionRepository.findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(
                1L, PaymentProvider.VNPAY, PaymentTransactionStatus.PENDING)).thenReturn(Optional.of(pendingTx));
        when(vnPayService.queryTransaction(1L, "1_1001"))
                .thenReturn(new VNPayService.VNPayQueryResult("01", null, null, "Transaction not found", "{}"));

        boolean result = paymentService.reconcilePendingVNPayPayment(1L);

        assertFalse(result);
        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    void reconcilePendingVNPayPayment_NoPendingTransaction_ReturnsFalse() {
        when(paymentTransactionRepository.findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(
                1L, PaymentProvider.VNPAY, PaymentTransactionStatus.PENDING)).thenReturn(Optional.empty());

        boolean result = paymentService.reconcilePendingVNPayPayment(1L);

        assertFalse(result);
        verify(vnPayService, never()).queryTransaction(anyLong(), anyString());
    }
}

package com.fashion.app.service.payment;

import com.fashion.app.dto.request.ProcessPaymentRequestDTO;
import com.fashion.app.dto.response.PaymentResponseDTO;

import java.util.Map;

public interface PaymentService {

    PaymentResponseDTO processPayment(ProcessPaymentRequestDTO dto);

    // Xử lý VNPay
    public void processVNPayIPN(Map<String, String> stringParams);
    String processVNPayReturn(Map<String, String> allParams);
    PaymentResponseDTO recreateVNPayPayment(Long orderId);

    /**
     * Chủ động tra cứu (query) lại giao dịch VNPay của 1 đơn hàng đang PENDING_PAYMENT trước khi để
     * scheduler hủy đơn do quá hạn — tránh case tiền đã trừ nhưng return/IPN bị mất do mất kết nối.
     * @return true nếu xác nhận giao dịch đã thanh toán thành công (đơn đã được cập nhật sang PAID),
     *         false nếu chưa có kết quả thành công (an toàn để scheduler tiếp tục hủy đơn).
     */
    boolean reconcilePendingVNPayPayment(Long orderId);
}

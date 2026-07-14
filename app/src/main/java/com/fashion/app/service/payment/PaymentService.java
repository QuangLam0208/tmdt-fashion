package com.fashion.app.service.payment;

import com.fashion.app.dto.request.ProcessPaymentRequestDTO;
import com.fashion.app.dto.response.PaymentResponseDTO;

import java.util.Map;

public interface PaymentService {

    PaymentResponseDTO processPayment(ProcessPaymentRequestDTO dto);

    // Xử lý MoMo
    void processMomoIPN(Map<String, Object> payload);
    String processMomoReturn(Map<String, String> allParams);
    PaymentResponseDTO recreateMomoPayment(Long orderId);
}

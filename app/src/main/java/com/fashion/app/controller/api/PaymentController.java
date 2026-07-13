package com.fashion.app.controller.api;

import com.fashion.app.dto.request.ProcessPaymentRequestDTO;
import com.fashion.app.dto.response.PaymentResponseDTO;
import com.fashion.app.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    /**
     * API xử lý thanh toán cho đơn hàng (Frontend gọi khi user bấm thanh toán)
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @Valid @RequestBody ProcessPaymentRequestDTO requestDTO) {
        PaymentResponseDTO response = paymentService.processPayment(requestDTO);
        return ResponseEntity.ok(response);
    }
}

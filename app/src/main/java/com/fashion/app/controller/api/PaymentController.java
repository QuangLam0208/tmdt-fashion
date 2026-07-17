package com.fashion.app.controller.api;

import com.fashion.app.dto.request.ProcessPaymentRequestDTO;
import com.fashion.app.dto.response.PaymentResponseDTO;
import com.fashion.app.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

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

    /**
     * API nhận Webhook/IPN từ hệ thống VNPay (Server-to-Server)
     * VNPay sẽ gọi vào endpoint này để cập nhật trạng thái giao dịch (thành công/thất bại) ngầm.
     * URL này phải được khai báo thủ công trong VNPay Merchant Admin (Sandbox), không truyền qua request tạo thanh toán.
     */
    @GetMapping("/vnpay/ipn")
    public ResponseEntity<String> processVNPayIPN(
            @RequestParam Map<String, String> allParams) {
        try {
            log.info("Nhận được IPN từ VNPay: {}", allParams);

            paymentService.processVNPayIPN(allParams);

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Lỗi khi xử lý IPN VNPay", e);
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * API xử lý URL Return từ VNPay (Trang chuyển hướng người dùng sau khi thanh toán xong trên cổng VNPay)
     */
    @GetMapping("/vnpay/return")
    public ResponseEntity<Void> processVNPayReturn(@RequestParam Map<String, String> allParams) {
        String status = paymentService.processVNPayReturn(allParams);
        String txnRef = allParams.get("vnp_TxnRef");
        String orderId = txnRef != null ? txnRef.split("_")[0] : null;

        String redirectUrl = frontendUrl + "checkout/payment-result?orderId=" + orderId + "&paymentStatus=" + status;

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl)).build();
    }

    /**
     * API tạo lại liên kết thanh toán VNPay cho đơn hàng (nếu đơn hàng chưa thanh toán và chọn VNPay)
     */
    @PostMapping("/vnpay/recreate/{orderId}")
    public ResponseEntity<PaymentResponseDTO> recreateVNPayPayment(@PathVariable Long orderId) {
        PaymentResponseDTO response = paymentService.recreateVNPayPayment(orderId);
        return ResponseEntity.ok(response);
    }
}

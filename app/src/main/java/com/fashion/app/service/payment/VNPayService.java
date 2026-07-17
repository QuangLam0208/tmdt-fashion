package com.fashion.app.service.payment;

import com.fashion.app.config.VNPayConfig;
import com.fashion.app.exception.BadRequestException;
import com.fashion.app.exception.ResourceNotFoundException;
import com.fashion.app.model.Order;
import com.fashion.app.model.PaymentTransaction;
import com.fashion.app.model.enums.PaymentProvider;
import com.fashion.app.model.enums.PaymentTransactionStatus;
import com.fashion.app.repository.OrderRepository;
import com.fashion.app.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final VNPayConfig vnPayConfig;
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RestClient restClient = RestClient.create();

    public record VNPayQueryResult(String responseCode, String transactionStatus, String transactionNo,
                                    String message, String rawResponse) {
        public boolean isSuccess() {
            return "00".equals(responseCode) && "00".equals(transactionStatus);
        }
    }

    /**
     * Tạo URL thanh toán VNPay (cổng vpcpay) để redirect người dùng sang trang thanh toán.
     * Mỗi lần gọi sinh 1 vnp_TxnRef mới (lưu vào PaymentTransaction.requestId) để đối soát/khớp
     * kết quả trả về (return/IPN) hoặc tra cứu lại (querydr) sau này.
     */
    public String createPaymentUrl(Long orderId, Double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại!"));

        String txnRef = orderId + "_" + System.currentTimeMillis();
        String orderInfo = "Thanh toan don hang " + orderId;
        long amountValue = Math.round(amount * 100);
        LocalDateTime now = LocalDateTime.now(VN_ZONE);

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnPayConfig.getVersion());
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnPayConfig.getTmnCode().trim());
        params.put("vnp_Amount", String.valueOf(amountValue));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        params.put("vnp_IpAddr", resolveClientIp());
        params.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        params.put("vnp_ExpireDate", now.plusMinutes(15).format(VNPAY_DATE_FORMAT));

        String paymentUrl = buildSignedUrl(vnPayConfig.getPayUrl(), params);

        paymentTransactionRepository.save(PaymentTransaction.builder()
                .order(order)
                .provider(PaymentProvider.VNPAY)
                .requestId(txnRef)
                .amount(amount)
                .status(PaymentTransactionStatus.PENDING)
                .rawResponsePayload("CREATED: " + params)
                .createdAt(Instant.now())
                .build());

        return paymentUrl;
    }

    /**
     * Chủ động tra cứu (querydr) trạng thái giao dịch thật với VNPay, dùng để đối soát khi return/IPN
     * có thể bị mất (case tiền đã trừ nhưng mất kết nối), trước khi scheduler hủy đơn do quá hạn.
     */
    public VNPayQueryResult queryTransaction(Long orderId, String txnRef) {
        Optional<PaymentTransaction> originalTx = paymentTransactionRepository
                .findByProviderAndRequestId(PaymentProvider.VNPAY, txnRef);

        if (originalTx.isEmpty()) {
            return new VNPayQueryResult(null, null, null, "Không tìm thấy giao dịch gốc để đối soát", "");
        }

        LocalDateTime transactionDate = LocalDateTime.ofInstant(originalTx.get().getCreatedAt(), VN_ZONE);
        LocalDateTime now = LocalDateTime.now(VN_ZONE);
        String requestId = String.valueOf(System.currentTimeMillis());
        String orderInfo = "Tra cuu giao dich don hang " + orderId;

        String hashData = String.join("|",
                requestId,
                vnPayConfig.getVersion(),
                "querydr",
                vnPayConfig.getTmnCode().trim(),
                txnRef,
                transactionDate.format(VNPAY_DATE_FORMAT),
                now.format(VNPAY_DATE_FORMAT),
                resolveClientIp(),
                orderInfo);
        String secureHash = hmacSha512(hashData, vnPayConfig.getHashSecret().trim());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("vnp_RequestId", requestId);
        body.put("vnp_Version", vnPayConfig.getVersion());
        body.put("vnp_Command", "querydr");
        body.put("vnp_TmnCode", vnPayConfig.getTmnCode().trim());
        body.put("vnp_TxnRef", txnRef);
        body.put("vnp_OrderInfo", orderInfo);
        body.put("vnp_TransactionDate", transactionDate.format(VNPAY_DATE_FORMAT));
        body.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        body.put("vnp_IpAddr", resolveClientIp());
        body.put("vnp_SecureHash", secureHash);

        try {
            Map<?, ?> response = restClient.post()
                    .uri(vnPayConfig.getApiUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String rawResponse = String.valueOf(response);
            String responseCode = response != null ? String.valueOf(response.get("vnp_ResponseCode")) : null;
            String transactionStatus = response != null ? String.valueOf(response.get("vnp_TransactionStatus")) : null;
            String transactionNo = response != null ? String.valueOf(response.get("vnp_TransactionNo")) : null;
            String message = response != null ? String.valueOf(response.get("vnp_Message")) : "Không nhận được phản hồi từ VNPay";

            return new VNPayQueryResult(responseCode, transactionStatus, transactionNo, message, rawResponse);
        } catch (Exception e) {
            log.error("Lỗi khi tra cứu giao dịch VNPay cho đơn #{}: {}", orderId, e.getMessage(), e);
            return new VNPayQueryResult(null, null, null, "Không thể kết nối đến VNPay: " + e.getMessage(), "ERROR: " + e.getMessage());
        }
    }

    /**
     * Xác thực chữ ký VNPay gửi về (Return URL hoặc IPN)
     */
    public boolean verifySignature(Map<String, String> allParams) {
        String receivedHash = allParams.get("vnp_SecureHash");
        if (receivedHash == null) return false;

        TreeMap<String, String> sortedParams = new TreeMap<>(allParams);
        sortedParams.remove("vnp_SecureHash");
        sortedParams.remove("vnp_SecureHashType");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
            if (hashData.length() > 0) hashData.append('&');
            hashData.append(entry.getKey()).append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        String recalculatedHash = hmacSha512(hashData.toString(), vnPayConfig.getHashSecret().trim());
        boolean valid = recalculatedHash.equalsIgnoreCase(receivedHash);
        if (!valid) {
            log.warn("Chữ ký VNPay không khớp cho vnp_TxnRef={}", allParams.get("vnp_TxnRef"));
        }
        return valid;
    }

    private String buildSignedUrl(String baseUrl, Map<String, String> sortedParams) {
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) continue;
            if (hashData.length() > 0) {
                hashData.append('&');
                query.append('&');
            }
            hashData.append(entry.getKey()).append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append('=')
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        String secureHash = hmacSha512(hashData.toString(), vnPayConfig.getHashSecret().trim());
        return baseUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String ip = attrs.getRequest().getHeader("X-Forwarded-For");
                if (ip != null && !ip.isBlank()) {
                    return ip.split(",")[0].trim();
                }
                return attrs.getRequest().getRemoteAddr();
            }
        } catch (Exception ignored) {
            // Không nằm trong 1 HTTP request (VD: chạy từ scheduler) -> fallback bên dưới
        }
        return "127.0.0.1";
    }

    private String hmacSha512(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHexString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo signature: " + e.getMessage());
        }
    }

    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }
}

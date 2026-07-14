package com.fashion.app.service.payment;

import com.fashion.app.config.MomoConfig;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoService {

    private final MomoConfig momoConfig;
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RestClient restClient = RestClient.create();

    public record MomoQueryResult(int resultCode, String transId, String message, String rawResponse) {
        public boolean isSuccess() {
            return resultCode == 0;
        }
    }

    /**
     * Gọi API "create" thật của MoMo (captureWallet) để lấy payUrl cho người dùng quét/redirect thanh toán.
     * Mỗi lần gọi sẽ tạo 1 requestId mới và lưu lại 1 PaymentTransaction (PENDING) để sau này đối soát (queryTransaction)
     * hoặc khớp với IPN/return trả về.
     */
    public String createPaymentUrl(Long orderId, Double amount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại!"));

        String accessKey = momoConfig.getAccessKey().trim();
        String partnerCode = momoConfig.getPartnerCode().trim();
        String secretKey = momoConfig.getSecretKey().trim();

        String requestId = UUID.randomUUID().toString();
        String orderIdStr = String.valueOf(orderId);
        String orderInfo = "Thanh toan don hang " + orderId;
        String requestType = "captureWallet";
        String extraData = "";
        long amountValue = amount.longValue();

        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amountValue +
                "&extraData=" + extraData +
                "&ipnUrl=" + momoConfig.getNotifyUrl() +
                "&orderId=" + orderIdStr +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + momoConfig.getReturnUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = hmacSha256(rawSignature, secretKey);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("accessKey", accessKey);
        body.put("requestId", requestId);
        body.put("amount", amountValue);
        body.put("orderId", orderIdStr);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", momoConfig.getReturnUrl());
        body.put("ipnUrl", momoConfig.getNotifyUrl());
        body.put("extraData", extraData);
        body.put("requestType", requestType);
        body.put("signature", signature);
        body.put("lang", "vi");

        String rawResponse;
        int resultCode = -1;
        String payUrl = null;
        String message;
        try {
            Map<?, ?> response = restClient.post()
                    .uri(momoConfig.getApiUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            rawResponse = String.valueOf(response);
            if (response != null && response.get("resultCode") != null) {
                resultCode = ((Number) response.get("resultCode")).intValue();
            }
            payUrl = response != null ? (String) response.get("payUrl") : null;
            message = response != null ? String.valueOf(response.get("message")) : "Không nhận được phản hồi từ MoMo";
        } catch (Exception e) {
            log.error("Lỗi khi gọi API tạo giao dịch MoMo cho đơn #{}: {}", orderId, e.getMessage(), e);
            rawResponse = "ERROR: " + e.getMessage();
            message = "Không thể kết nối đến MoMo: " + e.getMessage();
        }

        boolean success = resultCode == 0 && payUrl != null;

        paymentTransactionRepository.save(PaymentTransaction.builder()
                .order(order)
                .provider(PaymentProvider.MOMO)
                .requestId(requestId)
                .amount(amount)
                .status(success ? PaymentTransactionStatus.PENDING : PaymentTransactionStatus.FAILED)
                .rawResponsePayload(rawResponse)
                .createdAt(Instant.now())
                .build());

        if (!success) {
            throw new BadRequestException("Không thể tạo giao dịch thanh toán MoMo: " + message);
        }

        return payUrl;
    }

    /**
     * Chủ động tra cứu trạng thái giao dịch thật với MoMo (endpoint "query"), dùng để đối soát
     * khi IPN có thể đã bị mất/timeout (case tiền đã trừ nhưng mất kết nối).
     * Tra cứu dựa trên requestId của lần tạo giao dịch (PENDING) gần nhất của đơn hàng.
     */
    public MomoQueryResult queryTransaction(Long orderId, String requestId) {
        String accessKey = momoConfig.getAccessKey().trim();
        String partnerCode = momoConfig.getPartnerCode().trim();
        String secretKey = momoConfig.getSecretKey().trim();
        String orderIdStr = String.valueOf(orderId);

        String rawSignature = "accessKey=" + accessKey +
                "&orderId=" + orderIdStr +
                "&partnerCode=" + partnerCode +
                "&requestId=" + requestId;

        String signature = hmacSha256(rawSignature, secretKey);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("accessKey", accessKey);
        body.put("requestId", requestId);
        body.put("orderId", orderIdStr);
        body.put("signature", signature);
        body.put("lang", "vi");

        try {
            Map<?, ?> response = restClient.post()
                    .uri(momoConfig.getQueryUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String rawResponse = String.valueOf(response);
            int resultCode = response != null && response.get("resultCode") != null
                    ? ((Number) response.get("resultCode")).intValue() : -1;
            String transId = response != null && response.get("transId") != null
                    ? String.valueOf(response.get("transId")) : null;
            String message = response != null ? String.valueOf(response.get("message")) : "Không nhận được phản hồi từ MoMo";

            return new MomoQueryResult(resultCode, transId, message, rawResponse);
        } catch (Exception e) {
            log.error("Lỗi khi tra cứu giao dịch MoMo cho đơn #{}: {}", orderId, e.getMessage(), e);
            return new MomoQueryResult(-1, null, "Không thể kết nối đến MoMo: " + e.getMessage(), "ERROR: " + e.getMessage());
        }
    }

    /**
     * Xác thực chữ ký MoMo gửi về (IPN hoặc Return URL)
     */
    public boolean verifySignature(Map<String, String> allParams) {
        String signature = allParams.get("signature");
        if (signature == null) return false;

        // Các tham số để tạo chữ ký verify (theo thứ tự alphabet của Key)
        // accessKey, amount, extraData, message, orderId, orderInfo, orderType, partnerCode, requestId, responseTime, resultCode, transId
        String rawSignature = "accessKey=" + allParams.get("accessKey") +
                "&amount=" + allParams.get("amount") +
                "&extraData=" + allParams.get("extraData") +
                "&message=" + allParams.get("message") +
                "&orderId=" + allParams.get("orderId") +
                "&orderInfo=" + allParams.get("orderInfo") +
                "&orderType=" + allParams.get("orderType") +
                "&partnerCode=" + allParams.get("partnerCode") +
                "&requestId=" + allParams.get("requestId") +
                "&responseTime=" + allParams.get("responseTime") +
                "&resultCode=" + allParams.get("resultCode") +
                "&transId=" + allParams.get("transId");

        String recalculatingSecretKey = momoConfig.getSecretKey().trim();
        String recalculatedSignature = hmacSha256(rawSignature, recalculatingSecretKey);
        boolean valid = recalculatedSignature.equalsIgnoreCase(signature);
        if (!valid) {
            log.warn("Chữ ký MoMo không khớp cho orderId={}, requestId={}", allParams.get("orderId"), allParams.get("requestId"));
        }
        return valid;
    }

    /**
     * Tạo chữ ký HMAC-SHA256
     */
    private String hmacSha256(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
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

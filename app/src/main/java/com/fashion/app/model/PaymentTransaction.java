package com.fashion.app.model;

import com.fashion.app.model.enums.PaymentProvider;
import com.fashion.app.model.enums.PaymentTransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Ghi nhận từng giao dịch cổng thanh toán (VNPay) gắn với 1 Order.
 * Là chốt chặn idempotency: mỗi transId từ cổng thanh toán chỉ được xử lý (cộng dồn trạng thái đơn) đúng 1 lần,
 * kể cả khi IPN bị cổng thanh toán gọi lại nhiều lần (retry chuẩn khi không nhận được HTTP 200 kịp thời).
 */
@Entity
@Table(name = "payment_transactions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_transaction_provider_trans_id", columnNames = { "provider", "trans_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider;

    // requestId sinh ra khi tạo giao dịch (createPaymentUrl) — dùng để đối soát/query lại với cổng thanh toán
    @Column(name = "request_id", length = 100)
    private String requestId;

    // transId cổng thanh toán trả về khi giao dịch có kết quả (IPN/return/query) — null cho tới lúc đó
    @Column(name = "trans_id", length = 100)
    private String transId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentTransactionStatus status;

    // Lưu nguyên payload phản hồi/gọi từ cổng thanh toán để đối soát sau này (audit log)
    @Column(name = "raw_response_payload", columnDefinition = "TEXT")
    private String rawResponsePayload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;
}

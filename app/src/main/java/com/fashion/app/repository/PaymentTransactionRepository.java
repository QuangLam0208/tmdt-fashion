package com.fashion.app.repository;

import com.fashion.app.model.PaymentTransaction;
import com.fashion.app.model.enums.PaymentProvider;
import com.fashion.app.model.enums.PaymentTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    // Chốt chặn idempotency: 1 transId của 1 provider chỉ tồn tại 1 bản ghi duy nhất (ràng buộc unique ở DB)
    boolean existsByProviderAndTransId(PaymentProvider provider, String transId);

    Optional<PaymentTransaction> findByProviderAndTransId(PaymentProvider provider, String transId);

    // Lấy giao dịch PENDING gần nhất của 1 đơn hàng để đối soát (query lại cổng thanh toán)
    Optional<PaymentTransaction> findFirstByOrderIdAndProviderAndStatusOrderByCreatedAtDesc(
            Long orderId, PaymentProvider provider, PaymentTransactionStatus status);

    Optional<PaymentTransaction> findByProviderAndRequestId(PaymentProvider provider, String requestId);
}

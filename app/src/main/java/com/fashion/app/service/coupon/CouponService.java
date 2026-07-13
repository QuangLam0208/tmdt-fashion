package com.fashion.app.service.coupon;

import com.fashion.app.dto.request.ApplyCouponRequestDTO;
import com.fashion.app.dto.request.CollectCouponRequestDTO;
import com.fashion.app.dto.request.CreateCouponRequestDTO;
import com.fashion.app.dto.request.UpdateCouponRequestDTO;
import com.fashion.app.dto.response.ApplyCouponResponseDTO;
import com.fashion.app.dto.response.CouponResponseDTO;
import com.fashion.app.dto.response.MessageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CouponService {
    // Xem danh sách mã giảm giá đang có
    List<CouponResponseDTO> getAvailableCoupons(Long userId);

    // Thu thập mã giảm giá
    MessageResponseDTO collectCoupon(Long userId, CollectCouponRequestDTO dto);

    // Áp dụng mã giảm giá cho đơn hàng
    ApplyCouponResponseDTO applyCoupon(Long userId, ApplyCouponRequestDTO dto, Double currentTotal);

    // Admin
    Page<CouponResponseDTO> getAllCoupons(String keyword, Pageable pageable);

    CouponResponseDTO getCouponDetail(Long couponId);

    CouponResponseDTO createCoupon(CreateCouponRequestDTO dto);

    CouponResponseDTO updateCoupon(Long couponId, UpdateCouponRequestDTO dto);

    MessageResponseDTO toggleCouponStatus(Long couponId);
    /// Lấy danh sách ví voucher của user
    List<CouponResponseDTO> getMyWallet(Long userId);
}

package com.fashion.app.controller.api;

import com.fashion.app.dto.request.ApplyCouponRequestDTO;
import com.fashion.app.dto.request.CollectCouponRequestDTO;
import com.fashion.app.dto.response.ApplyCouponResponseDTO;
import com.fashion.app.dto.response.CouponResponseDTO;
import com.fashion.app.dto.response.MessageResponseDTO;
import com.fashion.app.service.coupon.CouponService;
import com.fashion.app.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // XEM DANH SÁCH MÃ
    @GetMapping("/list")
    public ResponseEntity<List<CouponResponseDTO>> getAvailableCoupons() {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(couponService.getAvailableCoupons(userId));
    }

    // THU THẬP MÃ GIẢM GIÁ
    @PostMapping("/collect")
    public ResponseEntity<MessageResponseDTO> collectCoupon(
            @Valid @RequestBody CollectCouponRequestDTO dto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(couponService.collectCoupon(userId, dto));
    }

    // ÁP DỤNG MÃ CHO ĐƠN HÀNG
    @PostMapping("/apply")
    public ResponseEntity<ApplyCouponResponseDTO> applyCoupon(
            @RequestParam Double currentTotal,
            @Valid @RequestBody ApplyCouponRequestDTO dto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.status(HttpStatus.OK)
                .body(couponService.applyCoupon(userId, dto, currentTotal));
    }
    @GetMapping("/wallet")
    public ResponseEntity<List<CouponResponseDTO>> getMyWalletCoupons() {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(couponService.getMyWallet(userId));
    }
}

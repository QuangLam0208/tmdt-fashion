package com.fashion.app.controller.api;

import com.fashion.app.dto.request.SubmitReviewRequestDTO;
import com.fashion.app.dto.response.ProductReviewListResponseDTO;
import com.fashion.app.dto.response.ReviewResponseDTO;
import com.fashion.app.service.review.ReviewService;
import com.fashion.app.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // THÊM ĐÁNH GIÁ
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> submitReview(
            @Valid @RequestBody SubmitReviewRequestDTO dto) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.submitReview(userId, dto));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ReviewResponseDTO>> getMyReviews(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId, pageable));
    }

    // XEM ĐÁNH GIÁ CỦA SẢN PHẨM
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductReviewListResponseDTO> getReviewsByProduct(
            @PathVariable("productId") Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, pageable));
    }
}

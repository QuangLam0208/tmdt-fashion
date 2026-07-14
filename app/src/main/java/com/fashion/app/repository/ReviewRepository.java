package com.fashion.app.repository;

import com.fashion.app.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Hiển thị danh sách đánh giá của 1 sản phẩm
    List<Review> findByProductId(Long productId);

    @EntityGraph(attributePaths = {"product", "user", "orderItem", "orderItem.order"})
    Page<Review> findByProductId(Long productId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "user", "orderItem", "orderItem.order"})
    Page<Review> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "user", "orderItem", "orderItem.order"})
    Page<Review> findAll(Pageable pageable);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    long countByProductId(Long productId);
}

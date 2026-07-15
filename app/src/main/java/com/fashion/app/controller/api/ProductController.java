package com.fashion.app.controller.api;

import com.fashion.app.dto.response.CategoryResponseDTO;
import com.fashion.app.dto.response.ProductDetailResponseDTO;
import com.fashion.app.dto.response.ProductSummaryResponseDTO;
import com.fashion.app.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ========== PUBLIC ==========

    /**
     * GET /api/products?keyword=áo&page=0&size=12
     */
    @GetMapping("/list")
    public ResponseEntity<Page<ProductSummaryResponseDTO>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        String[] sortParts = sort.split(",");
        String sortProperty = sortParts[0];
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1])) {
            sortDirection = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));
        return ResponseEntity.ok(productService.getProducts(keyword, categoryId, pageable));
    }

    /**
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponseDTO> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }

    /**
     * GET /api/products/{id}/related?limit=4
     */
    @GetMapping("/{id}/related")
    public ResponseEntity<List<ProductSummaryResponseDTO>> getRelatedProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "4") int limit
    ) {
        return ResponseEntity.ok(productService.getRelatedProducts(id, limit));
    }

    /**
     * GET /api/products/for-you?userId=1
     */
    @GetMapping("/for-you")
    public ResponseEntity<List<ProductSummaryResponseDTO>> getRecommendations(
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(productService.getRecommendations(userId));
    }

    /**
     * GET /api/products/categories
     * Dùng cho dropdown chọn danh mục khi tạo/lọc sản phẩm
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(productService.getAllCategories());
    }

}
package com.fashion.app.service.product;

import com.fashion.app.dto.request.CreateProductRequestDTO;
import com.fashion.app.dto.request.UpdateProductRequestDTO;
import com.fashion.app.dto.response.CategoryResponseDTO;
import com.fashion.app.dto.response.ProductDetailResponseDTO;
import com.fashion.app.dto.response.ProductSummaryResponseDTO;
import com.fashion.app.model.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    // Public: danh sách sản phẩm đang bán
    Page<ProductSummaryResponseDTO> getProducts(String keyword, Long categoryId, Pageable pageable);

    // Admin: danh sách có thể lọc theo status
    Page<ProductSummaryResponseDTO> getAdminProducts(String keyword, Long categoryId, ProductStatus status, Pageable pageable);

    // Chi tiết 1 sản phẩm
    ProductDetailResponseDTO getProductDetail(Long productId);

    // Sản phẩm liên quan
    List<ProductSummaryResponseDTO> getRelatedProducts(Long productId, int limit);

    // Sản phẩm gợi ý (Recommendation System)
    List<ProductSummaryResponseDTO> getRecommendations(Long userId);


    // CRUD (Admin)
    ProductDetailResponseDTO createProduct(CreateProductRequestDTO dto);
    ProductDetailResponseDTO updateProduct(Long productId, UpdateProductRequestDTO dto);
    void deleteProduct(Long productId);
    List<CategoryResponseDTO> getAllCategories();
}

package com.fashion.app.service.category;

import com.fashion.app.dto.request.CategoryRequestDTO;
import com.fashion.app.dto.response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {

    List<CategoryResponseDTO> getCategoryTree();

    CategoryResponseDTO getCategoryById(Long id);

    CategoryResponseDTO getCategoryByName(String name);

    CategoryResponseDTO createCategory(CategoryRequestDTO request);

    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request);

    void deleteCategory(Long id);
    List<CategoryResponseDTO> searchCategoriesByName(String keyword);
}
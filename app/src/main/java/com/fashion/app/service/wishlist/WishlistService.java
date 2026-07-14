package com.fashion.app.service.wishlist;

import com.fashion.app.dto.response.WishlistItemResponseDTO;
import com.fashion.app.dto.response.WishlistToggleResponseDTO;

import java.util.List;

public interface WishlistService {
    // Quản lý mục yêu thích
    List<WishlistItemResponseDTO> getWishlist(Long userId);
    WishlistToggleResponseDTO toggleWishlist(Long userId, Long productId);
    void removeWishlistItems(Long userId, List<Long> wishlistItemId);
}

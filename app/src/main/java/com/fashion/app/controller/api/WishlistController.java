package com.fashion.app.controller.api;

import com.fashion.app.dto.request.RemoveWishlistRequestDTO;
import com.fashion.app.dto.response.WishlistItemResponseDTO;
import com.fashion.app.dto.response.WishlistToggleResponseDTO;
import com.fashion.app.service.wishlist.WishlistService;
import com.fashion.app.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // LẤY DANH SÁCH YÊU THÍCH
    @GetMapping("/list")
    public ResponseEntity<List<WishlistItemResponseDTO>> getWishlist() {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }

    // TOGGLE YÊU THÍCH
    @PostMapping("/toggle")
    public ResponseEntity<WishlistToggleResponseDTO> toggleWishlist(
            @RequestParam Long productId
    ) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(
                wishlistService.toggleWishlist(userId, productId)
        );
    }

    // XÓA KHỎI DANH SÁCH
    @DeleteMapping("remove")
    public ResponseEntity<Void> removeListWishlistItem(@RequestBody RemoveWishlistRequestDTO wishlists) {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        wishlistService.removeWishlistItems(userId, wishlists.getWishlists());
        return ResponseEntity.noContent().build();
    }
}

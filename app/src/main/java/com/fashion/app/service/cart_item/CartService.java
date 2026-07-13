package com.fashion.app.service.cart_item;

import com.fashion.app.dto.request.AddToCartRequestDTO;
import com.fashion.app.dto.request.UpdateCartItemRequestDTO;
import com.fashion.app.dto.response.CartItemResponseDTO;
import com.fashion.app.dto.response.CartResponseDTO;
import com.fashion.app.dto.response.MessageResponseDTO;

public interface CartService {
    // Quản lý giỏ hàng - Xem giỏ hàng
    CartResponseDTO getCartItems(Long userId);

    // Thêm vào giỏ hàng
    CartItemResponseDTO addToCart(Long userId, AddToCartRequestDTO dto);

    // Cập nhật số lượng sản phẩm trong giỏ
    CartItemResponseDTO updateCartItem(Long userId, UpdateCartItemRequestDTO dto);

    // Xóa sản phẩm khỏi giỏ hàng
    MessageResponseDTO removeCartItem(Long userId, Long cartItemId);
}

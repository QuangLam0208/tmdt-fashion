package com.fashion.app.controller.api;

import com.fashion.app.dto.request.PlaceOrderRequestDTO;
import com.fashion.app.dto.response.*;
import com.fashion.app.service.order.OrderService;
import com.fashion.app.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ĐẶT HÀNG
    @PostMapping
    public ResponseEntity<PlaceOrderResponseDTO> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDTO dto) {
        // Luôn ghi đè userId từ session để đảm bảo bảo mật
        dto.setUserId(SecurityUtils.getAuthenticatedUserId());
        PlaceOrderResponseDTO response = orderService.placeOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

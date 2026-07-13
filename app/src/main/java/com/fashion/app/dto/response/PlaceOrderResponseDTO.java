package com.fashion.app.dto.response;

import com.fashion.app.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderResponseDTO {
    private Long orderId;
    private Double totalAmount;
    private OrderStatus status;
    private String message;
    private String paymentUrl;
}

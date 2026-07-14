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
public class OrderItemPreviewDTO {
    private String productName;
    private String productImage;
    private Long quantity;
    private OrderStatus orderItemStatus;
}

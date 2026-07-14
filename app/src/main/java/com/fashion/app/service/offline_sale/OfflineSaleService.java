package com.fashion.app.service.offline_sale;

import com.fashion.app.dto.request.RecordOfflineSaleRequestDTO;
import com.fashion.app.dto.response.PlaceOrderResponseDTO;

public interface OfflineSaleService {
    // Ghi nhận bán hàng trực tiếp
    PlaceOrderResponseDTO recordOfflineSale(RecordOfflineSaleRequestDTO dto);
}

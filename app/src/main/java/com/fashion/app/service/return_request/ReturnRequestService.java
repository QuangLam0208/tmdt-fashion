package com.fashion.app.service.return_request;

import com.fashion.app.dto.request.ProcessReturnRequestDTO;
import com.fashion.app.dto.request.SubmitReturnRequestDTO;
import com.fashion.app.dto.response.MessageResponseDTO;
import com.fashion.app.dto.response.ReturnRequestDetailResponseDTO;
import com.fashion.app.dto.response.ReturnRequestListItemResponseDTO;
import com.fashion.app.model.Order;
import com.fashion.app.model.OrderItem;
import com.fashion.app.model.ReturnRequest;
import com.fashion.app.model.enums.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReturnRequestService {
    // Lấy danh sách đơn hàng đang có yêu cầu hoàn trả của khách hàng
    List<ReturnRequestListItemResponseDTO> getReturnRequestsByCustomer(Long customerId);
    ReturnRequestDetailResponseDTO getCustomerReturnRequestDetail(Long requestId);
    Order getOrderForReturn(Long orderId);
    List<OrderItem> validateReturnEligibility(Long orderId, List<Long> itemIds);
    ReturnRequest submitReturnRequest(SubmitReturnRequestDTO dto);

    // Admin
    Page<ReturnRequestListItemResponseDTO> getAllReturnRequests(ReturnStatus status, Pageable pageable);
    ReturnRequestDetailResponseDTO getReturnRequestDetail(Long requestId);
    MessageResponseDTO processReturnRequest(Long requestId, ProcessReturnRequestDTO dto);
}

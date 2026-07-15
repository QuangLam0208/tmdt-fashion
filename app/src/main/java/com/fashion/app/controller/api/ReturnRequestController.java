package com.fashion.app.controller.api;

import com.fashion.app.dto.request.SubmitReturnRequestDTO;
import com.fashion.app.dto.response.ReturnRequestDetailResponseDTO;
import com.fashion.app.dto.response.ReturnRequestListItemResponseDTO;
import com.fashion.app.model.ReturnRequest;
import com.fashion.app.service.return_request.ReturnRequestService;
import com.fashion.app.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return-requests")
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestService returnRequestService;

    // LẤY DS YÊU CẦU TRẢ HÀNG
    @GetMapping("/list")
    public ResponseEntity<List<ReturnRequestListItemResponseDTO>> getReturnRequestsByCustomer() {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(
                returnRequestService.getReturnRequestsByCustomer(userId));
    }

    // XEM CHI TIẾT (CUSTOMER)
    @GetMapping("/{requestId}")
    public ResponseEntity<ReturnRequestDetailResponseDTO> getCustomerReturnRequestDetail(
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(returnRequestService.getCustomerReturnRequestDetail(requestId));
    }

    // GỬI YÊU CẦU TRẢ HÀNG (JSON)
    @PostMapping
    public ResponseEntity<ReturnRequest> submitReturnRequest(
            @Valid @RequestBody SubmitReturnRequestDTO dto) {
        return ResponseEntity.ok(
                returnRequestService.submitReturnRequest(dto));
    }
}

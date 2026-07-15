package com.fashion.app.service.user;

import com.fashion.app.dto.request.ChangePasswordRequestDTO;
import com.fashion.app.dto.request.UpdateCustomerStatusRequestDTO;
import com.fashion.app.dto.request.UpdateProfileRequestDTO;
import com.fashion.app.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    // Quản lý thông tin cá nhân
    ProfileResponseDTO getProfile();
    ProfileResponseDTO updateProfile(UpdateProfileRequestDTO dto);
    // Đổi mật khẩu
    MessageResponseDTO changePassword(ChangePasswordRequestDTO dto);
    // Xóa tài khoản
    MessageResponseDTO deleteAccount();
    // Xác thực email
    MessageResponseDTO resendVerification();
    // Admin
    Page<CustomerSummaryResponseDTO> getAllCustomers(String keyword, Pageable pageable);

    CustomerDetailResponseDTO getCustomerDetail(Long customerId);

    OrderDetailResponseDTO getCustomerOrderDetail(Long customerId, Long orderId);

    MessageResponseDTO updateCustomerStatus(Long customerId, UpdateCustomerStatusRequestDTO dto);
}

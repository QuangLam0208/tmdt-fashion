package com.fashion.app.dto.response;

import com.fashion.app.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSummaryResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;
}
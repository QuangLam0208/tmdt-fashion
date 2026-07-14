package com.fashion.app.dto.response;

import com.fashion.app.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private Long userId;
    private String fullName;
    private String phone;
    private String email;
    private List<AddressResponseDTO> address;
    private String pendingEmail;
    private boolean emailVerified;
    private Role role;
}

package com.fashion.app.service.auth;


import com.fashion.app.dto.request.*;
import com.fashion.app.dto.response.LoginResponseDTO;
import com.fashion.app.dto.response.MessageResponseDTO;
import com.fashion.app.dto.response.RegisterResponseDTO;

public interface AuthService {
    // Đăng ký tài khoản
    RegisterResponseDTO registerNewAccount(RegisterRequestDTO dto);

    // Xác thực email
    void verifyEmail(String token);

    // Gửi lại xác thực
    MessageResponseDTO resendVerificationEmail(ResendVerificationEmailRequestDTO dto);

    // Đăng nhập
    LoginResponseDTO login(LoginRequestDTO dto);

    // Quên mật khẩu
    MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO dto);
    MessageResponseDTO resetPassword(ResetPasswordRequestDTO dto);

    // Đăng xuất
    MessageResponseDTO logout(LogoutRequestDTO dto);

    // Làm mới token
    LoginResponseDTO refreshToken(RefreshTokenRequestDTO dto);
}

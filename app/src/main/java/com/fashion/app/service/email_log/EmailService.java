package com.fashion.app.service.email_log;

public interface EmailService {
    Boolean sendVerificationEmail (String email, String token);
    Boolean sendResetPasswordEmail(String email, String token);
}


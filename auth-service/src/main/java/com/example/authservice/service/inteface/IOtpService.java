package com.example.authservice.service.inteface;


import com.example.authservice.request.EmailRequest;

public interface IOtpService {
    void generateAndSaveOtp(EmailRequest request);
    boolean validateOtp(String userId, String otp);
    void verifyOtp(String userId, String otpCode);
}

package com.example.authservice.service;

import com.example.authservice.request.EmailRequest;
import com.example.authservice.service.inteface.IOtpService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService implements IOtpService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final long OTP_EXPIRATION_MINUTES = 5;

    @Override
    public void generateAndSaveOtp(EmailRequest emailRequest) {
        String otpCode = generateOtp();
        String key = "otp:" + emailRequest.getEmail();
        redisTemplate.opsForValue().set(key, otpCode, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        System.out.println("OTP for user " + emailRequest.getEmail() + ": " + otpCode);

    }


    @Override
    public boolean validateOtp(String userId, String otp) {
        String key = "otp:" + userId;
        String savedOtp = redisTemplate.opsForValue().get(key);
        return otp.equals(savedOtp);
    }

    @Override
    public void verifyOtp(String userId, String otpCode) {
        if (!validateOtp(userId, otpCode)) {
            throw new RuntimeException("Invalid OTP");
        }
        redisTemplate.delete("otp:" + userId);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

}

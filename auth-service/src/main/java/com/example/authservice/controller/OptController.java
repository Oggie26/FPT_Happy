package com.example.authservice.controller;

import com.example.authservice.request.AuthRequest;
import com.example.authservice.request.EmailRequest;
import com.example.authservice.response.ApiResponse;
import com.example.authservice.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Validated
@RequestMapping("/api/otp")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Otp Controller")
public class OptController {

    private final OtpService otpService;

    @PostMapping("/forgot-password")
    @Operation(summary = "Yêu cầu gửi OTP", description = "Gửi mã OTP đến người dùng khi quên mật khẩu")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid EmailRequest request) {
        otpService.generateAndSaveOtp(request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Mã OTP đã được gửi đến email của bạn")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


    @PostMapping("/verify")
    @Operation(summary = "Xác minh OTP", description = "Xác minh mã OTP của người dùng")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @RequestParam String username,
            @RequestParam String otp
    ) {
        otpService.verifyOtp(username, otp);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message("Xác minh OTP thành công")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}

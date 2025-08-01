package com.example.authservice.controller;

import com.example.authservice.request.AuthRequest;
import com.example.authservice.request.RegisterRequest;
import com.example.authservice.request.UpdateRequest;
import com.example.authservice.response.ApiResponse;
import com.example.authservice.response.AuthResponse;
import com.example.authservice.response.LoginResponse;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@RestController
@RequestMapping("/api/auth")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@Tag(name = "Auth Controller")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Đăng nhập", description = "API đăng nhập")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid AuthRequest request) {
        return ResponseEntity.ok(ApiResponse.<LoginResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Đăng nhập thành công")
                .data(authService.authenticate(request))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng kí", description = "API đăng kí")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest){

        return ApiResponse.<AuthResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Đăng kí thành công")
                .data(authService.register(registerRequest))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/{username}")
    public ApiResponse<AuthResponse> getUserByUsername(@PathVariable String username) {
        AuthResponse authResponse = authService.getUserByUsername(username);
        return ApiResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin người dùng thành công")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }




}

package com.example.productservice.feign;

import com.example.productservice.request.AuthRequest;
import com.example.productservice.response.ApiResponse;
import com.example.productservice.response.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "auth-service")
public interface AuthClient {

    @PostMapping("/api/auth/register")
    ApiResponse<AuthResponse> register(@RequestBody AuthRequest authRequest);

    @GetMapping("/api/auth/{username}")
    ApiResponse<AuthResponse> getUserByUsername(@PathVariable("username") String username);

    @PatchMapping("/api/auth/changePassword")
    ApiResponse<AuthResponse> changePassword(@RequestBody String changePassword);
}

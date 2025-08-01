package com.example.authservice.service.inteface;

import com.example.authservice.request.AuthRequest;
import com.example.authservice.request.RegisterRequest;
import com.example.authservice.request.UpdateRequest;
import com.example.authservice.response.AuthResponse;
import com.example.authservice.response.LoginResponse;

import java.util.List;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    LoginResponse authenticate(AuthRequest request);
    AuthResponse getUserByUsername(String username);

}

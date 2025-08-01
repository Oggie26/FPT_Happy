package com.example.authservice.service;

import com.example.authservice.config.JwtService;
import com.example.authservice.entity.Auth;
import com.example.authservice.enums.EnumRole;
import com.example.authservice.enums.EnumStatus;
import com.example.authservice.enums.ErrorCode;
import com.example.authservice.event.UserCreatedEvent;
import com.example.authservice.exception.AppException;
import com.example.authservice.feign.UserClient;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.request.AuthRequest;
import com.example.authservice.request.RegisterRequest;
import com.example.authservice.response.*;
import com.example.authservice.service.inteface.IAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements IAuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthRepository authRepository;
    private final UserClient userClient;
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (request.getPassword().length() < 6) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        if (request.getUsername().length() < 6) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
        if (authRepository.findByUsernameAndIsDeletedFalse(request.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        Auth auth = Auth.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        authRepository.save(auth);

        UserCreatedEvent event = new UserCreatedEvent(auth.getId(), EnumRole.CUSTOMER);

        try {
            kafkaTemplate.send("user-created-topic", event);
        } catch (AppException e) {
            log.error("Failed to send Kafka event: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return AuthResponse.builder()
                .id(auth.getId())
                .username(auth.getUsername())
                .build();

    }

    @Override
    public LoginResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        Auth auth = authRepository.findByUsernameAndIsDeletedFalse(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));

        ApiResponse<UserInfo> response = userClient.getUserInfo(auth.getId());
        System.out.println(response);
        if (EnumStatus.INACTIVE.equals(response.getData().getStatus())) {
            throw new AppException(ErrorCode.USER_BLOCKED);
        }
        if (response.getData().getIsDeleted()) {
            throw new AppException(ErrorCode.USER_DELETED);
        }
        String token = jwtService.generateToken(auth.getUsername());
        return LoginResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthResponse getUserByUsername(String username) {
        Auth auth = authRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));

        return AuthResponse.builder()
                .id(auth.getId())
                .username(auth.getUsername())
                .password(auth.getPassword())
                .build();
    }
}
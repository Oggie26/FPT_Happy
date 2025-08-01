package com.example.userservice.service;

import com.example.userservice.entity.User;
import com.example.userservice.enums.EnumStatus;
import com.example.userservice.enums.ErrorCode;
import com.example.userservice.event.UserPlacedEvent;
import com.example.userservice.exception.AppException;
import com.example.userservice.feign.AuthClient;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.request.AuthRequest;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.*;
import com.example.userservice.service.inteface.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final AuthClient authClient;
    private final KafkaTemplate<String, UserPlacedEvent> kafkaTemplate;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.findByEmailAndIsDeletedFalse(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        if (userRepository.findByPhoneAndIsDeletedFalse(request.getPhone()).isPresent()) {
            throw new AppException(ErrorCode.PHONE_EXISTS);
        }

        AuthRequest authRequest = AuthRequest.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        ApiResponse<AuthResponse> response = authClient.register(authRequest);
        if (response == null || response.getData() == null) {
            throw new AppException(ErrorCode.EXTERNAL_SERVICE_ERROR);
        }

        return createAndSaveUser(request, response.getData());
    }

    public UserResponse createAndSaveUser(UserRequest request, AuthResponse authData) {
        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .gender(request.getGender())
                .fullName(request.getFullName())
                .birthday(request.getBirthday())
                .status(EnumStatus.ACTIVE)
                .role(request.getRole())
                .avatar(request.getAvatar())
                .authId(authData.getId())
                .build();

        userRepository.save(user);

//        UserPlacedEvent event = UserPlacedEvent.builder()
//                .id(user.getId())
//                .email(user.getEmail())
//                .phone(user.getPhone())
//                .fullName(user.getFullName())
//                .build();
//        try {
//            kafkaTemplate.send("user-topic", event);
//            log.info("Đã gửi Kafka event: {}", event);
//        } catch (Exception e) {
//            log.error("❌ Gửi Kafka thất bại: {}", e.getMessage(), e);
//        }
        publisher.publishEvent(new UserPlacedEvent(user.getId(), user.getEmail(), user.getPhone(), user.getFullName()));
        return toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserRequest request) {
        User updateUser = userRepository.findByIdAndIsDeletedFalse(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));

        userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .filter(u -> !u.getId().equals(request.getId()))
                .ifPresent(u -> {
                    throw new AppException(ErrorCode.EMAIL_EXISTS);
                });

        userRepository.findByPhoneAndIsDeletedFalse(request.getPhone())
                .filter(u -> !u.getId().equals(request.getId()))
                .ifPresent(u -> {
                    throw new AppException(ErrorCode.PHONE_EXISTS);
                });

        updateUser.setEmail(request.getEmail());
        updateUser.setPhone(request.getPhone());
        updateUser.setGender(request.getGender());
        updateUser.setFullName(request.getFullName());
        updateUser.setBirthday(request.getBirthday());
        updateUser.setAvatar(request.getAvatar());
        updateUser.setStatus(request.getStatus());

        userRepository.save(updateUser);
        return toUserResponse(updateUser);
    }

    @Override
    public void disableUser(String id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));
        user.setStatus(user.getStatus().equals(EnumStatus.INACTIVE) ? EnumStatus.ACTIVE : EnumStatus.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));
        return toUserResponse(user);
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.getIsDeleted())
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserInfo getUserInfo(String authId) {
        User user = userRepository.findByAuthId(authId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));
        return UserInfo.builder()
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .isDeleted(user.getIsDeleted())
                .build();
    }

    @Override
    public PageResponse<UserResponse> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.searchByKeywordNative(keyword, pageable);

        List<UserResponse> data = userPage.getContent().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                data,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );
    }

    @Override
    public UserResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = authentication.getName();
        ApiResponse<AuthResponse> response = authClient.getUserByUsername(username);

        if (response == null || response.getData() == null) {
            throw new AppException(ErrorCode.NOT_FOUND_USER);
        }

        User user = userRepository.findByAuthId(response.getData().getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));

        return toUserResponse(user);
    }

    @Override
    public UserResponse updateProfile(UserRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = authentication.getName();
        ApiResponse<AuthResponse> response = authClient.getUserByUsername(username);

        if (response == null || response.getData() == null) {
            throw new AppException(ErrorCode.NOT_FOUND_USER);
        }

        User user = userRepository.findByAuthId(response.getData().getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));

        userRepository.findByEmailAndIsDeletedFalse(userRequest.getEmail())
                .filter(u -> !u.getId().equals(userRequest.getId()))
                .ifPresent(u -> {
                    throw new AppException(ErrorCode.EMAIL_EXISTS);
                });

        userRepository.findByPhoneAndIsDeletedFalse(userRequest.getPhone())
                .filter(u -> !u.getId().equals(userRequest.getId()))
                .ifPresent(u -> {
                    throw new AppException(ErrorCode.PHONE_EXISTS);
                });

        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setGender(userRequest.getGender());
        user.setFullName(userRequest.getFullName());
        user.setBirthday(userRequest.getBirthday());
        user.setAvatar(userRequest.getAvatar());
        userRepository.save(user);

        return toUserResponse(user);
    }

    @Override
    public void changePassword(ChangePassword password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = authentication.getName();
        ApiResponse<AuthResponse> response = authClient.getUserByUsername(username);

        if (response == null || response.getData() == null) {
            throw new AppException(ErrorCode.NOT_FOUND_USER);
        }

        AuthResponse user = response.getData();

        if (!passwordEncoder.matches(password.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        AuthRequest updateRequest = AuthRequest.builder()
                .password(passwordEncoder.encode(password.getNewPassword()))
                .build();

        authClient.changePassword(updateRequest.getPassword());
    }


    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .birthday(user.getBirthday())
                .gender(user.getGender())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .status(user.getStatus())
                .build();
    }
}

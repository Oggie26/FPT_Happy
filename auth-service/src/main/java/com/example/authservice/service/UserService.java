package com.example.authservice.service;

import com.example.authservice.entity.Auth;
import com.example.authservice.enums.ErrorCode;
import com.example.authservice.exception.AppException;
import com.example.authservice.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Auth auth = authRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_USER));

        return org.springframework.security.core.userdetails.User
                .withUsername(auth.getUsername())
                .password(auth.getPassword())
                .build();
    }
}




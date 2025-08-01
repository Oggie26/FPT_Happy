package com.example.productservice.request;

import com.example.productservice.enums.EnumRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequest {
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private EnumRole role;
}

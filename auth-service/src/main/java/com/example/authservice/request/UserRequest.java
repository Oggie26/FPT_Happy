package com.example.authservice.request;

import com.example.authservice.enums.EnumRole;
import com.example.authservice.enums.EnumStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Builder
@Data
public class UserRequest {
    String id;
    String fullName;
    String password;
    String username;
    String email;
    String phone;
    String avatar;
    Boolean gender;
    String authId;
    Date birthday;
    @Enumerated(EnumType.STRING)
    EnumRole role;
    @Enumerated(EnumType.STRING)
    EnumStatus status;
}

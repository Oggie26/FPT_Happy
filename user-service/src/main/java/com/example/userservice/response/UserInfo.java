package com.example.userservice.response;

import com.example.userservice.enums.EnumRole;
import com.example.userservice.enums.EnumStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserInfo{
    String fullName;
    @Enumerated(EnumType.STRING)
    EnumStatus status;
    @Enumerated(EnumType.STRING)
    EnumRole role;
    Boolean isDeleted;
}

package com.example.authservice.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserResponse {
    String id;
    String fullName;
    String email;
    String phone;
    Boolean gender;
    Date birthday;
    String avatar;
}

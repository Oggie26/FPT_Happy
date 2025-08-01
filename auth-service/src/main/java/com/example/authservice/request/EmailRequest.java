package com.example.authservice.request;

import com.example.authservice.util.ValidEmail;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailRequest {
    String email;
}

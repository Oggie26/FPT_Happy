package com.example.authservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String token = SecurityContextHolder.getContext().getAuthentication() != null
                ? (String) SecurityContextHolder.getContext().getAuthentication().getCredentials()
                : null;

        if (token != null) {
            template.header("Authorization", "Bearer " + token);
        }
    }
}

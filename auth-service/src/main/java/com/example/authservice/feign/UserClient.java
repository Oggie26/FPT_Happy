package com.example.authservice.feign;

import com.example.authservice.config.FeignClientConfig;
import com.example.authservice.request.UserRequest;
import com.example.authservice.response.ApiResponse;
import com.example.authservice.response.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserClient {
    @GetMapping("/api/users/info/{authId}")
    ApiResponse<UserInfo> getUserInfo(@PathVariable String authId);

    @PostMapping("/api/users")
    void createUser(@RequestBody UserRequest request);

}

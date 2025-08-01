package com.example.userservice.service.inteface;

import com.example.userservice.entity.User;
import com.example.userservice.request.UserRequest;
import com.example.userservice.response.ChangePassword;
import com.example.userservice.response.PageResponse;
import com.example.userservice.response.UserInfo;
import com.example.userservice.response.UserResponse;

import java.util.List;

public interface IUserService {
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(UserRequest userRequest);
    void disableUser(String id);
    void deleteUser(String id);
    UserResponse getUserById(String id);
    List<UserResponse> getUsers();
    UserInfo getUserInfo(String authId);
    PageResponse<UserResponse> searchUsers(String request, int page, int size);
    UserResponse getProfile();
    UserResponse updateProfile(UserRequest userRequest);
    void changePassword(ChangePassword password);
}

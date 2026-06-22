package com.unibuc.authservice.service;

import com.unibuc.authservice.dto.request.RegisterRequest;
import com.unibuc.authservice.dto.request.UpdateUserRequest;
import com.unibuc.authservice.dto.response.UserResponse;
import com.unibuc.authservice.exception.DuplicateEmailException;

import java.util.List;

public interface UserService {
    UserResponse getCurrentUser();
    UserResponse registerStoreOwner(RegisterRequest input) throws DuplicateEmailException;
    List<UserResponse> getStoreOwners();
    UserResponse updateCurrentUser(UpdateUserRequest request);
}

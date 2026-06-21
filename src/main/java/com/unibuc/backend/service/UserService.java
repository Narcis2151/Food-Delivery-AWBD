package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.RegisterRequest;
import com.unibuc.backend.dto.request.UpdateUserRequest;
import com.unibuc.backend.dto.response.UserResponse;
import com.unibuc.backend.exception.DuplicateEmailException;

import java.util.List;

public interface UserService {
    UserResponse getCurrentUser();
    UserResponse registerStoreOwner(RegisterRequest input) throws DuplicateEmailException;
    List<UserResponse> getStoreOwners();
    UserResponse updateCurrentUser(UpdateUserRequest request);
}

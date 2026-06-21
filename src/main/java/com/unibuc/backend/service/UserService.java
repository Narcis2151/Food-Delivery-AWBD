package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.UpdateUserRequest;
import com.unibuc.backend.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser();
    UserResponse updateCurrentUser(UpdateUserRequest request);
}

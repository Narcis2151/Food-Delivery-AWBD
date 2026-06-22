package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.LoginRequest;
import com.unibuc.backend.dto.request.RegisterRequest;
import com.unibuc.backend.exception.DuplicateEmailException;
import com.unibuc.backend.exception.InvalidCredentialsException;
import com.unibuc.backend.model.User;

public interface AuthenticationService {
    User registerCustomer(RegisterRequest input) throws DuplicateEmailException;

    User authenticate(LoginRequest input) throws InvalidCredentialsException;
}

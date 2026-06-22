package com.unibuc.authservice.service;

import com.unibuc.authservice.dto.request.LoginRequest;
import com.unibuc.authservice.dto.request.RegisterRequest;
import com.unibuc.authservice.exception.DuplicateEmailException;
import com.unibuc.authservice.exception.InvalidCredentialsException;
import com.unibuc.authservice.model.User;

public interface AuthenticationService {
    User registerCustomer(RegisterRequest input) throws DuplicateEmailException;

    User authenticate(LoginRequest input) throws InvalidCredentialsException;
}

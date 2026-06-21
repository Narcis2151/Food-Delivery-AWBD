package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.LoginRequest;
import com.unibuc.backend.dto.request.RegisterRequest;
import com.unibuc.backend.exception.DuplicateEmailException;
import com.unibuc.backend.exception.InvalidCredentialsException;
import com.unibuc.backend.exception.NotExistentRoleException;
import com.unibuc.backend.model.ERole;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.RoleRepository;
import com.unibuc.backend.repository.UserRepository;
import com.unibuc.backend.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    @Override
    public User registerCustomer(RegisterRequest input) throws DuplicateEmailException {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new DuplicateEmailException();
        }
        var role = roleRepository.findByName(ERole.ROLE_CUSTOMER).orElseThrow(NotExistentRoleException::new);

        var user = new User(
                input.getFullName(),
                input.getEmail(),
                passwordEncoder.encode(input.getPassword()),
                role
        );

        return userRepository.save(user);
    }

    @Override
    public User registerStoreOwner(RegisterRequest input) throws DuplicateEmailException {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new DuplicateEmailException();
        }
        var role = roleRepository.findByName(ERole.ROLE_STORE_OWNER).orElseThrow(NotExistentRoleException::new);

        var user = new User(
                input.getFullName(),
                input.getEmail(),
                passwordEncoder.encode(input.getPassword()),
                role
        );

        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginRequest input) throws InvalidCredentialsException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
        return userRepository.findByEmail(input.getEmail()).orElseThrow(InvalidCredentialsException::new);

    }
}

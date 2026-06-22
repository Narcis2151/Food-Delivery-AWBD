package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.LoginRequest;
import com.unibuc.backend.dto.request.RegisterRequest;
import com.unibuc.backend.exception.DuplicateEmailException;
import com.unibuc.backend.exception.InvalidCredentialsException;
import com.unibuc.backend.exception.NotExistentRoleException;
import com.unibuc.backend.model.ERole;
import com.unibuc.backend.model.Role;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.RoleRepository;
import com.unibuc.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks AuthenticationServiceImpl authenticationService;

    private Role customerRole() {
        return Role.builder().id(1).name(ERole.ROLE_CUSTOMER).build();
    }

    @Test
    void registerCustomer_whenEmailAlreadyExists_throwsDuplicateEmailException() {
        RegisterRequest req = new RegisterRequest("Test User", "test@mail.com", "pass");
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.registerCustomer(req))
                .isInstanceOf(DuplicateEmailException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerCustomer_whenRoleNotFound_throwsNotExistentRoleException() {
        RegisterRequest req = new RegisterRequest("Test User", "test@mail.com", "pass");
        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.registerCustomer(req))
                .isInstanceOf(NotExistentRoleException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerCustomer_whenValidRequest_encodesPasswordAndSavesUser() {
        Role role = customerRole();
        RegisterRequest req = new RegisterRequest("John Doe", "john@mail.com", "plaintext");
        when(userRepository.existsByEmail("john@mail.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_CUSTOMER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("plaintext")).thenReturn("hashed");
        User saved = new User("John Doe", "john@mail.com", "hashed", role);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = authenticationService.registerCustomer(req);

        assertThat(result.getEmail()).isEqualTo("john@mail.com");
        assertThat(result.getPassword()).isEqualTo("hashed");
        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void authenticate_whenCredentialsValid_returnsUser() {
        Role role = customerRole();
        User user = new User("John", "john@mail.com", "hashed", role);
        LoginRequest req = new LoginRequest("john@mail.com", "plaintext");
        when(userRepository.findByEmail("john@mail.com")).thenReturn(Optional.of(user));

        User result = authenticationService.authenticate(req);

        assertThat(result.getEmail()).isEqualTo("john@mail.com");
    }

    @Test
    void authenticate_whenAuthManagerThrows_throwsInvalidCredentialsException() {
        LoginRequest req = new LoginRequest("john@mail.com", "wrongpass");
        doThrow(new BadCredentialsException("bad credentials")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authenticationService.authenticate(req))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticate_whenUserNotFoundAfterAuth_throwsInvalidCredentialsException() {
        LoginRequest req = new LoginRequest("ghost@mail.com", "pass");
        when(userRepository.findByEmail("ghost@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(req))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}

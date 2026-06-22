package com.unibuc.authservice.service.impl;

import com.unibuc.authservice.model.ERole;
import com.unibuc.authservice.model.Role;
import com.unibuc.authservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    private static final String TEST_SECRET = "e3cbc7cca57d111913d50b0c0b4075b5bc2e6265da68bcdaf325b9132b8eb57e";
    private static final long TEST_EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);
    }

    private User testUser() {
        Role role = Role.builder().id(1).name(ERole.ROLE_CUSTOMER).build();
        return User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john@mail.com")
                .password("encoded")
                .role(role)
                .build();
    }

    @Test
    void generateToken_returnsNonNullNonBlankToken() {
        String token = jwtService.generateToken(testUser());
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUsername_returnsUserEmail() {
        User user = testUser();
        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("john@mail.com");
    }

    @Test
    void isTokenValid_withMatchingUser_returnsTrue() {
        User user = testUser();
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_returnsFalse() {
        User user = testUser();
        Role role = Role.builder().id(1).name(ERole.ROLE_CUSTOMER).build();
        User other = User.builder().id(2L).fullName("Other").email("other@mail.com").password("pw").role(role).build();
        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }

    @Test
    void getExpirationTime_returnsConfiguredValue() {
        assertThat(jwtService.getExpirationTime()).isEqualTo(TEST_EXPIRATION);
    }

    @Test
    void generateToken_withExtraClaims_stillContainsCorrectSubject() {
        User user = testUser();
        java.util.Map<String, Object> claims = java.util.Map.of("customKey", "customValue");
        String token = jwtService.generateToken(claims, user);

        assertThat(jwtService.extractUsername(token)).isEqualTo("john@mail.com");
    }
}

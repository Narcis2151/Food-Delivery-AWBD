package com.unibuc.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibuc.backend.dto.request.LoginRequest;
import com.unibuc.backend.dto.request.RegisterRequest;
import com.unibuc.backend.model.ERole;
import com.unibuc.backend.model.Role;
import com.unibuc.backend.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationE2ETest {

    @Autowired MockMvc mockMvc;
    @Autowired RoleRepository roleRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SIGNUP_URL = "/api/v1/auth/register";
    private static final String LOGIN_URL  = "/api/v1/auth/login";

    @BeforeEach
    void seedRoles() {
        roleRepository.saveAll(List.of(
                Role.builder().name(ERole.ROLE_CUSTOMER).build(),
                Role.builder().name(ERole.ROLE_STORE_OWNER).build(),
                Role.builder().name(ERole.ROLE_ADMIN).build()
        ));
    }

    private RegisterRequest customerRequest(String email) {
        return new RegisterRequest("Test User", email, "Password123!");
    }

    private String signupAndGetToken(String email) throws Exception {
        String body = mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest(email))))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    void signup_withValidRequest_returns200AndJwtToken() throws Exception {
        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest("john@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    void signup_withDuplicateEmail_returns400() throws Exception {
        RegisterRequest req = customerRequest("john@test.com");

        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_afterSignup_returns200AndJwtToken() throws Exception {
        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest("john@test.com"))))
                .andExpect(status().isOk());

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("john@test.com", "Password123!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        mockMvc.perform(post(SIGNUP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest("john@test.com"))))
                .andExpect(status().isOk());

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("john@test.com", "WrongPassword!"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/addresses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withValidToken_returns200() throws Exception {
        String token = signupAndGetToken("john@test.com");

        mockMvc.perform(get("/api/v1/addresses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

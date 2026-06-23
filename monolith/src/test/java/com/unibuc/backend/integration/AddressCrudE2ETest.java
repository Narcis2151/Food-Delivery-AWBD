package com.unibuc.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unibuc.backend.dto.request.AddressRequest;
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

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AddressCrudE2ETest {

    @Autowired MockMvc mockMvc;
    @Autowired RoleRepository roleRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void seedRoles() {
        roleRepository.saveAll(List.of(
                Role.builder().name(ERole.ROLE_CUSTOMER).build(),
                Role.builder().name(ERole.ROLE_STORE_OWNER).build(),
                Role.builder().name(ERole.ROLE_ADMIN).build()
        ));
    }

    private String getAuthToken() throws Exception {
        RegisterRequest req = new RegisterRequest("Test User", "test@mail.com", "Password123!");
        String body = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    private AddressRequest bucharestAddress() {
        return new AddressRequest(
                "123 Main St", "Bucharest", "Ilfov", "Romania",
                new BigDecimal("44.4268000"), new BigDecimal("26.1025000")
        );
    }

    private long createAddress(String token, AddressRequest req) throws Exception {
        String body = mockMvc.perform(post("/api/v1/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void createAddress_returns201WithPersistedData() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(post("/api/v1/addresses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bucharestAddress())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.street").value("123 Main St"))
                .andExpect(jsonPath("$.city").value("Bucharest"))
                .andExpect(jsonPath("$.country").value("Romania"));
    }

    @Test
    void getAddress_afterCreate_returnsCorrectData() throws Exception {
        String token = getAuthToken();
        long id = createAddress(token, bucharestAddress());

        mockMvc.perform(get("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.street").value("123 Main St"))
                .andExpect(jsonPath("$.city").value("Bucharest"))
                .andExpect(jsonPath("$.state").value("Ilfov"))
                .andExpect(jsonPath("$.country").value("Romania"));
    }

    @Test
    void updateAddress_reflectsNewValues() throws Exception {
        String token = getAuthToken();
        long id = createAddress(token, bucharestAddress());

        AddressRequest updated = new AddressRequest(
                "456 Park Ave", "Cluj-Napoca", "Cluj", "Romania",
                new BigDecimal("46.7712000"), new BigDecimal("23.6236000")
        );

        mockMvc.perform(put("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.street").value("456 Park Ave"))
                .andExpect(jsonPath("$.city").value("Cluj-Napoca"))
                .andExpect(jsonPath("$.state").value("Cluj"));
    }

    @Test
    void deleteAddress_thenGet_returns404() throws Exception {
        String token = getAuthToken();
        long id = createAddress(token, bucharestAddress());

        mockMvc.perform(delete("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/addresses/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAddress_withNonExistentId_returns404() throws Exception {
        String token = getAuthToken();

        mockMvc.perform(get("/api/v1/addresses/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}

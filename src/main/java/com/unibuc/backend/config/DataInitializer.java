package com.unibuc.backend.config;

import com.unibuc.backend.model.ERole;
import com.unibuc.backend.model.Role;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.RoleRepository;
import com.unibuc.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email:admin@fooddelivery.local}")
    private String adminEmail;

    @Value("${app.bootstrap.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.bootstrap.admin.full-name:System Admin}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdmin();
    }

    private void seedRoles() {
        Arrays.stream(ERole.values()).forEach(roleName -> {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
                log.info("Created role {}", roleName);
            }
        });
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN was not seeded"));

        userRepository.save(new User(
                adminFullName,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                adminRole
        ));

        log.info("Created default admin user with email {}", adminEmail);
    }
}

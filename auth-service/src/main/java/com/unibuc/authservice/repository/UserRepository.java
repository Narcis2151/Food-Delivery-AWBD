package com.unibuc.authservice.repository;

import com.unibuc.authservice.model.ERole;
import com.unibuc.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole_Name(ERole role);
    boolean existsByEmail(String email);
}

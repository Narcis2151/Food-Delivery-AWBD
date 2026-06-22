package com.unibuc.backend.repository;

import com.unibuc.backend.model.ERole;
import com.unibuc.backend.model.User;
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

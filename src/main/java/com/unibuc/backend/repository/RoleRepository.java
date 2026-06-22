package com.unibuc.backend.repository;

import com.unibuc.backend.model.ERole;
import com.unibuc.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

}

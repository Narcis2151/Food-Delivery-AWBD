package com.unibuc.backend.repository;

import com.unibuc.backend.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Page<Store> findByOwnerId(Long ownerId, Pageable pageable);
}

package com.unibuc.storeservice.repository;

import com.unibuc.storeservice.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByOwnerId(Long ownerId);
}

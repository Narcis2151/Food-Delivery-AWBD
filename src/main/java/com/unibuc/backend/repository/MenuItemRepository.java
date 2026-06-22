package com.unibuc.backend.repository;

import com.unibuc.backend.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByStoreId(Long storeId, Pageable pageable);
    Page<MenuItem> findByCategoryId(Long categoryId, Pageable pageable);
}

package com.unibuc.backend.repository;

import com.unibuc.backend.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByStoreId(Long storeId);
    List<MenuItem> findByCategoryId(Long categoryId);
}

package com.unibuc.backend.repository;

import com.unibuc.backend.model.MenuItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemCategoryRepository extends JpaRepository<MenuItemCategory, Long> {
    List<MenuItemCategory> findByStoreId(Long storeId);
}

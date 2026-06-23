package com.unibuc.reviewservice.repository;

import com.unibuc.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMenuItemId(Long menuItemId);
    List<Review> findByUserId(Long userId);
}

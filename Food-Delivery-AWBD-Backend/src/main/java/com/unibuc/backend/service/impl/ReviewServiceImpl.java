package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.ReviewRequest;
import com.unibuc.backend.dto.response.ReviewResponse;
import com.unibuc.backend.exception.MenuItemNotFoundException;
import com.unibuc.backend.exception.ReviewNotFoundException;
import com.unibuc.backend.model.MenuItem;
import com.unibuc.backend.model.Review;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.MenuItemRepository;
import com.unibuc.backend.repository.ReviewRepository;
import com.unibuc.backend.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> findAll() {
        return reviewRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> findByMenuItemId(Long menuItemId) {
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new MenuItemNotFoundException(menuItemId);
        }
        return reviewRepository.findByMenuItemId(menuItemId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse findById(Long id) {
        return toResponse(reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id)));
    }

    @Override
    public ReviewResponse create(ReviewRequest request) {
        User currentUser = getCurrentUser();
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new MenuItemNotFoundException(request.getMenuItemId()));
        Review review = Review.builder()
                .user(currentUser)
                .menuItem(menuItem)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        return toResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse update(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        assertIsAuthor(review);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return toResponse(reviewRepository.save(review));
    }

    @Override
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        assertIsAuthor(review);
        reviewRepository.delete(review);
    }

    private void assertIsAuthor(Review review) {
        User currentUser = getCurrentUser();
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only modify your own reviews.");
        }
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userFullName(review.getUser().getFullName())
                .menuItemId(review.getMenuItem().getId())
                .menuItemName(review.getMenuItem().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

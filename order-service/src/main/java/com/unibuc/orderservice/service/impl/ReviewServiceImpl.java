package com.unibuc.orderservice.service.impl;

import com.unibuc.orderservice.client.AuthServiceClient;
import com.unibuc.orderservice.client.StoresServiceClient;
import com.unibuc.orderservice.dto.request.ReviewRequest;
import com.unibuc.orderservice.dto.response.ReviewResponse;
import com.unibuc.orderservice.exception.MenuItemNotFoundException;
import com.unibuc.orderservice.exception.ReviewNotFoundException;
import com.unibuc.orderservice.model.Review;
import com.unibuc.orderservice.repository.ReviewRepository;
import com.unibuc.orderservice.security.SecurityUtils;
import com.unibuc.orderservice.service.ReviewService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoresServiceClient storesServiceClient;
    private final AuthServiceClient authServiceClient;

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
        validateMenuItemExists(menuItemId);
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
        validateMenuItemExists(request.getMenuItemId());
        Review review = Review.builder()
                .userId(SecurityUtils.currentUser().getId())
                .menuItemId(request.getMenuItemId())
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

    private void validateMenuItemExists(Long menuItemId) {
        try {
            storesServiceClient.getMenuItemById(menuItemId);
        } catch (FeignException.NotFound e) {
            throw new MenuItemNotFoundException(menuItemId);
        }
    }

    private void assertIsAuthor(Review review) {
        Long currentUserId = SecurityUtils.currentUser().getId();
        if (!review.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("You can only modify your own reviews.");
        }
    }

    private String resolveUserFullName(Long userId) {
        try {
            return authServiceClient.getUserById(userId).getFullName();
        } catch (Exception e) {
            log.warn("Could not resolve full name for userId={}: {}", userId, e.getMessage());
            return null;
        }
    }

    private String resolveMenuItemName(Long menuItemId) {
        try {
            return storesServiceClient.getMenuItemById(menuItemId).getName();
        } catch (Exception e) {
            log.warn("Could not resolve menu item name for menuItemId={}: {}", menuItemId, e.getMessage());
            return null;
        }
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userFullName(resolveUserFullName(review.getUserId()))
                .menuItemId(review.getMenuItemId())
                .menuItemName(resolveMenuItemName(review.getMenuItemId()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

package com.unibuc.reviewservice.service.impl;

import com.unibuc.reviewservice.client.dto.MenuItemDto;
import com.unibuc.reviewservice.dto.request.ReviewRequest;
import com.unibuc.reviewservice.dto.response.ReviewResponse;
import com.unibuc.reviewservice.exception.MenuItemNotFoundException;
import com.unibuc.reviewservice.exception.ReviewNotFoundException;
import com.unibuc.reviewservice.model.Review;
import com.unibuc.reviewservice.repository.ReviewRepository;
import com.unibuc.reviewservice.security.SecurityUtils;
import com.unibuc.reviewservice.service.RemoteDataResolver;
import com.unibuc.reviewservice.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final RemoteDataResolver remoteDataResolver;

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
        remoteDataResolver.requireMenuItem(menuItemId);
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
        Long currentUserId = SecurityUtils.currentUser().getId();
        MenuItemDto menuItem = remoteDataResolver.requireMenuItem(request.getMenuItemId());
        String authorName = remoteDataResolver.resolveUserFullName(currentUserId);

        Review review = Review.builder()
                .userId(currentUserId)
                .userFullName(authorName)
                .menuItemId(menuItem.getId())
                .menuItemName(menuItem.getName())
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
        Long currentUserId = SecurityUtils.currentUser().getId();
        if (!currentUserId.equals(review.getUserId())) {
            throw new AccessDeniedException("You can only modify your own reviews.");
        }
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userFullName(review.getUserFullName())
                .menuItemId(review.getMenuItemId())
                .menuItemName(review.getMenuItemName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

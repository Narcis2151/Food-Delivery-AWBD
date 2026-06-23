package com.unibuc.reviewservice.service;

import com.unibuc.reviewservice.dto.request.ReviewRequest;
import com.unibuc.reviewservice.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> findAll();
    List<ReviewResponse> findByMenuItemId(Long menuItemId);
    ReviewResponse findById(Long id);
    ReviewResponse create(ReviewRequest request);
    ReviewResponse update(Long id, ReviewRequest request);
    void delete(Long id);
}

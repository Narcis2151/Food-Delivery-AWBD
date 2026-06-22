package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.ReviewRequest;
import com.unibuc.backend.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> findAll();
    List<ReviewResponse> findByMenuItemId(Long menuItemId);
    ReviewResponse findById(Long id);
    ReviewResponse create(ReviewRequest request);
    ReviewResponse update(Long id, ReviewRequest request);
    void delete(Long id);
}

package com.unibuc.orderservice.service;

import com.unibuc.orderservice.dto.request.ReviewRequest;
import com.unibuc.orderservice.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> findAll();
    List<ReviewResponse> findByMenuItemId(Long menuItemId);
    ReviewResponse findById(Long id);
    ReviewResponse create(ReviewRequest request);
    ReviewResponse update(Long id, ReviewRequest request);
    void delete(Long id);
}

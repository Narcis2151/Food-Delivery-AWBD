package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.StoreRequest;
import com.unibuc.backend.dto.response.StoreResponse;

import java.util.List;

public interface StoreService {
    List<StoreResponse> findAll();
    StoreResponse findById(Long id);
    StoreResponse create(StoreRequest request);
    StoreResponse update(Long id, StoreRequest request);
    void delete(Long id);
}

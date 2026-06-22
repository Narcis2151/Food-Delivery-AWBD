package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.CreateStoreRequest;
import com.unibuc.backend.dto.request.UpdateStoreRequest;
import com.unibuc.backend.dto.response.PageResponse;
import com.unibuc.backend.dto.response.StoreResponse;
import org.springframework.data.domain.Pageable;

public interface StoreService {
    PageResponse<StoreResponse> findAll(Pageable pageable);
    PageResponse<StoreResponse> findMine(Pageable pageable);
    StoreResponse findById(Long id);
    StoreResponse create(CreateStoreRequest request);
    StoreResponse update(Long id, UpdateStoreRequest request);
    void delete(Long id);
}

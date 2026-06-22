package com.unibuc.storeservice.service;

import com.unibuc.storeservice.dto.request.CreateStoreRequest;
import com.unibuc.storeservice.dto.request.UpdateStoreRequest;
import com.unibuc.storeservice.dto.response.PageResponse;
import com.unibuc.storeservice.dto.response.StoreResponse;
import org.springframework.data.domain.Pageable;

public interface StoreService {
    PageResponse<StoreResponse> findAll(Pageable pageable);
    StoreResponse findMine();
    StoreResponse findById(Long id);
    StoreResponse create(CreateStoreRequest request);
    StoreResponse update(Long id, UpdateStoreRequest request);
    void delete(Long id);
}

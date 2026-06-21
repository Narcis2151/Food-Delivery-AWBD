package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.CreateStoreRequest;
import com.unibuc.backend.dto.request.UpdateStoreRequest;
import com.unibuc.backend.dto.response.StoreResponse;

import java.util.List;

public interface StoreService {
    List<StoreResponse> findAll();
    List<StoreResponse> findMine();
    StoreResponse findById(Long id);
    StoreResponse create(CreateStoreRequest request);
    StoreResponse update(Long id, UpdateStoreRequest request);
    void delete(Long id);
}

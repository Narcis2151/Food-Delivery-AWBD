package com.unibuc.backend.service;

import com.unibuc.backend.dto.request.AddressRequest;
import com.unibuc.backend.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> findAll();
    AddressResponse findById(Long id);
    AddressResponse create(AddressRequest request);
    AddressResponse update(Long id, AddressRequest request);
    void delete(Long id);
}

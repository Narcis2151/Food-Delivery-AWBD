package com.unibuc.authservice.service;

import com.unibuc.authservice.dto.request.AddressRequest;
import com.unibuc.authservice.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> findAll();
    AddressResponse findById(Long id);
    AddressResponse create(AddressRequest request);
    AddressResponse update(Long id, AddressRequest request);
    void delete(Long id);
}

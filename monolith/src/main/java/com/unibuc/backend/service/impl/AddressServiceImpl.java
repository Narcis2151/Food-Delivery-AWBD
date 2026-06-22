package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.AddressRequest;
import com.unibuc.backend.dto.response.AddressResponse;
import com.unibuc.backend.exception.AddressNotFoundException;
import com.unibuc.backend.model.Address;
import com.unibuc.backend.repository.AddressRepository;
import com.unibuc.backend.service.AddressService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AddressResponse> findAll() {
        return addressRepository.findAll().stream()
                .map(a -> modelMapper.map(a, AddressResponse.class))
                .toList();
    }

    @Override
    public AddressResponse findById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
        return modelMapper.map(address, AddressResponse.class);
    }

    @Override
    public AddressResponse create(AddressRequest request) {
        Address address = modelMapper.map(request, Address.class);
        return modelMapper.map(addressRepository.save(address), AddressResponse.class);
    }

    @Override
    public AddressResponse update(Long id, AddressRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
        modelMapper.map(request, address);
        return modelMapper.map(addressRepository.save(address), AddressResponse.class);
    }

    @Override
    public void delete(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new AddressNotFoundException(id);
        }
        addressRepository.deleteById(id);
    }
}

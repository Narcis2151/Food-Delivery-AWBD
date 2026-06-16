package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.StoreRequest;
import com.unibuc.backend.dto.response.AddressResponse;
import com.unibuc.backend.dto.response.StoreResponse;
import com.unibuc.backend.exception.AddressNotFoundException;
import com.unibuc.backend.exception.StoreNotFoundException;
import com.unibuc.backend.exception.UserNotFoundException;
import com.unibuc.backend.model.Address;
import com.unibuc.backend.model.Store;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.AddressRepository;
import com.unibuc.backend.repository.StoreRepository;
import com.unibuc.backend.repository.UserRepository;
import com.unibuc.backend.service.StoreService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
@Transactional
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<StoreResponse> findAll() {
        return storeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse findById(Long id) {
        return toResponse(storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id)));
    }

    @Override
    public StoreResponse create(StoreRequest request) {
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException(request.getAddressId()));
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));
        Store store = Store.builder()
                .name(request.getName())
                .address(address)
                .contactPhoneNumber(request.getContactPhoneNumber())
                .owner(owner)
                .build();
        return toResponse(storeRepository.save(store));
    }

    @Override
    public StoreResponse update(Long id, StoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException(request.getAddressId()));
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));
        store.setName(request.getName());
        store.setAddress(address);
        store.setContactPhoneNumber(request.getContactPhoneNumber());
        store.setOwner(owner);
        return toResponse(storeRepository.save(store));
    }

    @Override
    public void delete(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new StoreNotFoundException(id);
        }
        storeRepository.deleteById(id);
    }

    private StoreResponse toResponse(Store store) {
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(modelMapper.map(store.getAddress(), AddressResponse.class))
                .contactPhoneNumber(store.getContactPhoneNumber())
                .ownerId(store.getOwner().getId())
                .ownerFullName(store.getOwner().getFullName())
                .build();
    }
}

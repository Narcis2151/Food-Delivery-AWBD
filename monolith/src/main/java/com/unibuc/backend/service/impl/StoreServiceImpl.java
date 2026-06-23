package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.CreateStoreRequest;
import com.unibuc.backend.dto.request.UpdateStoreRequest;
import com.unibuc.backend.dto.response.AddressResponse;
import com.unibuc.backend.dto.response.PageResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResponse<StoreResponse> findAll(Pageable pageable) {
        return PageResponse.from(storeRepository.findAll(pageable), this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StoreResponse> findMine(Pageable pageable) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return PageResponse.from(storeRepository.findByOwnerId(currentUser.getId(), pageable), this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse findById(Long id) {
        return toResponse(storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id)));
    }

    @Override
    public StoreResponse create(CreateStoreRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException(request.getOwnerId()));

        Address address = null;
        if (request.getAddress() != null) {
            var a = request.getAddress();
            address = addressRepository.save(Address.builder()
                    .street(a.getStreet())
                    .city(a.getCity())
                    .state(a.getState())
                    .country(a.getCountry())
                    .latitude(a.getLatitude())
                    .longitude(a.getLongitude())
                    .build());
        }

        Store store = Store.builder()
                .name(request.getName())
                .owner(owner)
                .address(address)
                .contactPhoneNumber(request.getContactPhoneNumber())
                .build();
        return toResponse(storeRepository.save(store));
    }

    @Override
    public StoreResponse update(Long id, UpdateStoreRequest request) {
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
        AddressResponse addressResponse = store.getAddress() != null
                ? modelMapper.map(store.getAddress(), AddressResponse.class)
                : null;
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(addressResponse)
                .contactPhoneNumber(store.getContactPhoneNumber())
                .ownerEmail(store.getOwner().getEmail())
                .build();
    }
}

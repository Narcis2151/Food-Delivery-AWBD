package com.unibuc.storeservice.service.impl;

import com.unibuc.storeservice.client.dto.UserDto;
import com.unibuc.storeservice.dto.request.CreateStoreRequest;
import com.unibuc.storeservice.dto.request.UpdateStoreRequest;
import com.unibuc.storeservice.dto.response.AddressResponse;
import com.unibuc.storeservice.dto.response.PageResponse;
import com.unibuc.storeservice.dto.response.StoreResponse;
import com.unibuc.storeservice.exception.AddressNotFoundException;
import com.unibuc.storeservice.exception.StoreNotFoundException;
import com.unibuc.storeservice.model.Address;
import com.unibuc.storeservice.model.Store;
import com.unibuc.storeservice.repository.AddressRepository;
import com.unibuc.storeservice.repository.StoreRepository;
import com.unibuc.storeservice.security.SecurityUtils;
import com.unibuc.storeservice.service.OwnerResolver;
import com.unibuc.storeservice.service.StoreService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final AddressRepository addressRepository;
    private final OwnerResolver ownerResolver;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StoreResponse> findAll(Pageable pageable) {
        return PageResponse.from(storeRepository.findAll(pageable), this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse findMine() {
        Long currentUserId = SecurityUtils.currentUser().getId();
        return toResponse(storeRepository.findByOwnerId(currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public StoreResponse findById(Long id) {
        return toResponse(storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id)));
    }

    @Override
    public StoreResponse create(CreateStoreRequest request) {
        // Validate the owner exists in the auth-service (over OpenFeign).
        UserDto owner = ownerResolver.requireOwner(request.getOwnerId());

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
                .ownerId(owner.getId())
                .address(address)
                .contactPhoneNumber(request.getContactPhoneNumber())
                .build();
        return toResponse(storeRepository.save(store), owner.getEmail());
    }

    @Override
    public StoreResponse update(Long id, UpdateStoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AddressNotFoundException(request.getAddressId()));
        UserDto owner = ownerResolver.requireOwner(request.getOwnerId());
        store.setName(request.getName());
        store.setAddress(address);
        store.setContactPhoneNumber(request.getContactPhoneNumber());
        store.setOwnerId(owner.getId());
        return toResponse(storeRepository.save(store), owner.getEmail());
    }

    @Override
    public void delete(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new StoreNotFoundException(id);
        }
        storeRepository.deleteById(id);
    }

    private StoreResponse toResponse(Store store) {
        return toResponse(store, ownerResolver.resolveEmail(store.getOwnerId()));
    }

    private StoreResponse toResponse(Store store, String ownerEmail) {
        AddressResponse addressResponse = store.getAddress() != null
                ? modelMapper.map(store.getAddress(), AddressResponse.class)
                : null;
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .address(addressResponse)
                .contactPhoneNumber(store.getContactPhoneNumber())
                .ownerId(store.getOwnerId())
                .ownerEmail(ownerEmail)
                .build();
    }
}

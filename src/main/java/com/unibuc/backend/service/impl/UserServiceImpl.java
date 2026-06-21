package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.AddressRequest;
import com.unibuc.backend.dto.request.UpdateUserRequest;
import com.unibuc.backend.dto.response.AddressResponse;
import com.unibuc.backend.dto.response.UserResponse;
import com.unibuc.backend.exception.UserNotFoundException;
import com.unibuc.backend.model.Address;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.AddressRepository;
import com.unibuc.backend.repository.UserRepository;
import com.unibuc.backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        return toResponse(currentUser());
    }

    @Override
    public UserResponse updateCurrentUser(UpdateUserRequest request) {
        User user = currentUser();

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            AddressRequest a = request.getAddress();
            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            address.setStreet(a.getStreet());
            address.setCity(a.getCity());
            address.setState(a.getState());
            address.setCountry(a.getCountry());
            address.setLatitude(a.getLatitude());
            address.setLongitude(a.getLongitude());
            user.setAddress(addressRepository.save(address));
        }

        return toResponse(userRepository.save(user));
    }

    private User currentUser() {
        User principal = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new UserNotFoundException(principal.getId()));
    }

    private UserResponse toResponse(User user) {
        AddressResponse addressResponse = null;
        Address address = user.getAddress();
        if (address != null) {
            addressResponse = AddressResponse.builder()
                    .id(address.getId())
                    .street(address.getStreet())
                    .city(address.getCity())
                    .state(address.getState())
                    .country(address.getCountry())
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .build();
        }
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName().name() : null)
                .phoneNumber(user.getPhoneNumber())
                .address(addressResponse)
                .build();
    }
}

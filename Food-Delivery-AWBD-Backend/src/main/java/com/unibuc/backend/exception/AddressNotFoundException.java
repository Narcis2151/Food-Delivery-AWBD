package com.unibuc.backend.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(Long id) {
        super("Address with id " + id + " not found.");
    }
}

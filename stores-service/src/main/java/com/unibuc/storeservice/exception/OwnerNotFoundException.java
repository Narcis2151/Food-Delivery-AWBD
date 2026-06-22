package com.unibuc.storeservice.exception;

public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(Long id) {
        super("Owner with id " + id + " could not be resolved from the auth-service.");
    }
}

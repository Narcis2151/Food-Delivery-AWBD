package com.unibuc.backend.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(Long id) {
        super("Review with id " + id + " not found.");
    }
}

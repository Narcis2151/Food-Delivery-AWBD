package com.unibuc.authservice.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Account with this email already exists.");
    }
}

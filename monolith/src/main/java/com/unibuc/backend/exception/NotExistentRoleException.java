package com.unibuc.backend.exception;

public class NotExistentRoleException extends RuntimeException {

    public NotExistentRoleException() {
        super("This role does not exist.");
    }
}

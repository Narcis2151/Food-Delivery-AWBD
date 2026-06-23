package com.unibuc.reviewservice.exception;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(Long id) {
        super("Menu item with id " + id + " not found.");
    }
}

package com.unibuc.storeservice.exception;

public class MenuItemNotFoundException extends RuntimeException {
    public MenuItemNotFoundException(Long id) {
        super("Menu item with id " + id + " not found.");
    }
}

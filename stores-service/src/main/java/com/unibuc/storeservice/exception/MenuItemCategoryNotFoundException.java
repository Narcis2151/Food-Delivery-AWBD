package com.unibuc.storeservice.exception;

public class MenuItemCategoryNotFoundException extends RuntimeException {
    public MenuItemCategoryNotFoundException(Long id) {
        super("Menu item category with id " + id + " not found.");
    }
}

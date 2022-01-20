package com.github.papayankey.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Integer id) {
        super(String.format("Customer with id %d does not exist", id));
    }
}

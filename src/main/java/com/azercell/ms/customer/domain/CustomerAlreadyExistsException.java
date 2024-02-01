package com.azercell.ms.customer.domain;

public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String gsmNumber) {
        super("A customer with gsmNumber " + gsmNumber + " already exists.");
    }
}

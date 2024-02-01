package com.azercell.ms.customer.domain;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long customerId) {
        super("The customer with customerId " + customerId + " was not found.");
    }
}

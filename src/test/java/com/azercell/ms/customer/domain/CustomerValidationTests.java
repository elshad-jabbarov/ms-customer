package com.azercell.ms.customer.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        Customer customer = Customer.of("John", "Doe", LocalDate.of(1990, 1, 1), "+994501234567");
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenGsmNumberNotDefinedThenValidationFails() {
        Customer customer = Customer.of("John", "Doe", LocalDate.of(1990, 1, 1), "");
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("The GSM number must be defined."));
    }

    @Test
    void whenGsmNumberFormatIncorrectThenValidationFails() {
        Customer customer = Customer.of("John", "Doe", LocalDate.of(1990, 1, 1), "501234567");
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("The GSM number format must be valid."));
    }

    @Test
    void whenNameIsNotDefinedThenValidationFails() {
        Customer customer =
                new Customer(null, "", "Doe", LocalDate.of(1990, 1, 1), "+994501234567", BigDecimal.valueOf(100), null,
                        null, 0);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("The customer's name must be defined."));
    }

    // Additional tests for other fields like surname, birthDate, and balance
}

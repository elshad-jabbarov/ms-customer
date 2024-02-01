package com.azercell.ms.customer.web;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CustomerDTO(
        Long id,
        @NotBlank(message = "The customer's name must be defined.")
        String name,
        @NotBlank(message = "The customer's surname must be defined.")
        String surname,
        @NotNull(message = "The birth date must be defined.")
        LocalDate birthDate,
        @NotBlank(message = "The GSM number must be defined.")
        @Pattern(regexp = "^\\+994\\d{9}$", message = "The GSM number format must be valid.")
        String gsmNumber,
        @PositiveOrZero(message = "The balance must not be negative.")
        BigDecimal balance
) {
}

package com.azercell.ms.customer.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record Customer(
        @Id
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

        @NotNull(message = "The balance must be defined.")
        @PositiveOrZero(message = "The balance must not be negative.")
        BigDecimal balance,

        @CreatedDate
        Instant createdDate,

        @LastModifiedDate
        Instant lastModifiedDate,

        @Version
        int version

) {
    public static Customer of(String name, String surname, LocalDate birthDate, String gsmNumber) {
        return new Customer(null, name, surname, birthDate, gsmNumber, BigDecimal.valueOf(100),
                null, null, 0);
    }

    public Customer withBalance(BigDecimal newBalance) {
        return new Customer(this.id, this.name, this.surname, this.birthDate,
                this.gsmNumber, newBalance, this.createdDate,
                this.lastModifiedDate, this.version);
    }
}

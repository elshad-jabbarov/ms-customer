package com.azercell.ms.customer.config;

import com.azercell.ms.customer.domain.Customer;
import com.azercell.ms.customer.domain.CustomerRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("testData")
public class CustomerDataLoader {

    private final CustomerRepository customerRepository;

    public CustomerDataLoader(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadCustomerTestData() {
        customerRepository.deleteAll();

        var customer1 = Customer.of("Tural", "Bayramov", LocalDate.of(1990, 1,
                1), "+994501234567");
        var customer2 = Customer.of("Akif", "Huseyinli", LocalDate.of(1992, 2,
                1), "+994501234568");

        customerRepository.saveAll(List.of(customer1, customer2));
    }
}


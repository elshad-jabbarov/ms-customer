package com.azercell.ms.customer.domain;

import com.azercell.ms.customer.config.DataConfig;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
class CustomerRepositoryJdbcTests {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void whenCustomerExistsByGsmNumber_thenTrue() {
        var customer = new Customer(null, "John", "Doe",
                LocalDate.of(1990, 1, 1), "+994502234567", BigDecimal.valueOf(100),
                null, null, 0);
        jdbcAggregateTemplate.insert(customer);

        boolean exists = customerRepository.existsByGsmNumber("+994502234567");

        assertThat(exists).isTrue();
    }

    @Test
    void whenCustomerDoesNotExistByGsmNumber_thenFalse() {
        boolean exists = customerRepository.existsByGsmNumber("+994551234568");

        assertThat(exists).isFalse();
    }

    @Test
    void findAllCustomers() {
        var customer1 = new Customer(null, "John", "Doe", LocalDate.of(1990, 1,
                1), "+994701234569", BigDecimal.valueOf(100), null,
                null, 0);
        var customer2 = new Customer(null, "Jane", "Doe", LocalDate.of(1992, 2,
                2), "+994701234570", BigDecimal.valueOf(100), null,
                null, 0);
        jdbcAggregateTemplate.insert(customer1);
        jdbcAggregateTemplate.insert(customer2);

        Iterable<Customer> actualCustomers = customerRepository.findAll();

        assertThat(StreamSupport.stream(actualCustomers.spliterator(), false)
                .filter(customer -> customer.gsmNumber().equals(customer1.gsmNumber()) ||
                        customer.gsmNumber().equals(customer2.gsmNumber()))
                .collect(Collectors.toList())).hasSize(2);
    }
}


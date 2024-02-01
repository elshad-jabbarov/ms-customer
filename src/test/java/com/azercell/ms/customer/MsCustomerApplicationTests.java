package com.azercell.ms.customer;

import com.azercell.ms.customer.web.Amount;
import com.azercell.ms.customer.web.CustomerDTO;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class MsCustomerApplicationTests {


    private final String username = "user";
    private final String password = "password";
    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void setUp(@Autowired DataSource dataSource) {
        Flyway.configure().dataSource(dataSource).load().migrate();
    }

    @Test
    void whenPostRequestThenCustomerCreated() {
        CustomerDTO expectedCustomer = new CustomerDTO(null, "John", "Doe",
                LocalDate.of(1990, 1, 1), "+994501234567", BigDecimal.valueOf(100));

        webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .post()
                .uri("/customers")
                .bodyValue(expectedCustomer)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(CustomerDTO.class).value(actualCustomer -> {
                    assertThat(actualCustomer).isNotNull();
                    assertThat(actualCustomer.name()).isEqualTo(expectedCustomer.name());
                });
    }

    @Test
    void whenGetRequestWithIdThenCustomerReturned() {
        CustomerDTO customerToCreate = new CustomerDTO(null, "Jane", "Doe", LocalDate.of(1991, 2, 2), "+994501234568",
                BigDecimal.valueOf(100));
        CustomerDTO expectedCustomer = webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .post()
                .uri("/customers")
                .bodyValue(customerToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerDTO.class).returnResult().getResponseBody();

        assert expectedCustomer != null;
        webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .get()
                .uri("/customers/" + expectedCustomer.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class).value(actualCustomer -> {
                    assertThat(actualCustomer).isNotNull();
                    assertThat(actualCustomer.id()).isEqualTo(expectedCustomer.id());
                });
    }

    @Test
    void whenUnauthorizedThenReturnUnauthorized() {
        webTestClient
                .get()
                .uri("/customers")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void whenCustomerNotFoundThenReturnNotFound() {
        long nonExistentCustomerId = 999L;
        webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .get()
                .uri("/customers/" + nonExistentCustomerId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(String.class).value(errorMessage ->
                        assertThat(errorMessage).contains("The customer with customerId "
                                + nonExistentCustomerId + " was not found")
                );
    }

    @Test
    void whenPostRequestForTopUpBalanceThenBalanceUpdated() {
        CustomerDTO customerToCreate = new CustomerDTO(null, "Alice",
                "Wonder", LocalDate.of(1992, 3, 3), "+994501234569", BigDecimal.valueOf(100));
        CustomerDTO createdCustomer = webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .post()
                .uri("/customers")
                .bodyValue(customerToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerDTO.class).returnResult().getResponseBody();

        assert createdCustomer != null;
        BigDecimal topUpAmount = BigDecimal.valueOf(50);
        webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .post()
                .uri("/customers/" + createdCustomer.id() + "/topup")
                .bodyValue(new Amount(topUpAmount))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class).value(updatedCustomer -> {
                    assertThat(updatedCustomer).isNotNull();
                    assertThat(updatedCustomer.balance().doubleValue())
                            .isEqualTo(createdCustomer.balance().add(topUpAmount).doubleValue());
                });
    }

    @Test
    void whenPostRequestForPurchaseThenBalanceUpdated() {
        CustomerDTO customerToCreate =
                new CustomerDTO(null, "Bob", "Builder", LocalDate.of(1993, 4, 4), "+994501234570",
                        BigDecimal.valueOf(150));
        CustomerDTO createdCustomer = webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .post()
                .uri("/customers")
                .bodyValue(customerToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerDTO.class).returnResult().getResponseBody();

        assert createdCustomer != null;
        BigDecimal purchaseAmount = BigDecimal.valueOf(30);
        webTestClient
                .mutate().filter(basicAuthentication(username, password)).build()
                .post()
                .uri("/customers/" + createdCustomer.id() + "/purchase")
                .bodyValue(new Amount(purchaseAmount))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class).value(updatedCustomer -> {
                    assertThat(updatedCustomer).isNotNull();
                    assertThat(updatedCustomer.balance().doubleValue())
                            .isEqualTo(createdCustomer.balance().subtract(purchaseAmount).doubleValue());
                });
    }
}

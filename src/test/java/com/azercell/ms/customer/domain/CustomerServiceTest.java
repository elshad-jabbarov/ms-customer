package com.azercell.ms.customer.domain;


import com.azercell.ms.customer.componet.CustomerMapper;
import com.azercell.ms.customer.web.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void whenCustomerToAddAlreadyExistsThenThrows() {
        var gsmNumber = "+994501234567";
        var customerDTO = new CustomerDTO(null, "John", "Doe",
                LocalDate.of(1990, 1, 1), gsmNumber, new BigDecimal("100"));

        when(customerRepository.existsByGsmNumber(gsmNumber)).thenReturn(true);

        assertThatThrownBy(() -> customerService.addCustomer(customerDTO))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessage("A customer with gsmNumber " + gsmNumber + " already exists.");
    }

    @Test
    void whenCustomerToViewDetailsDoesNotExistThenThrows() {
        var customerId = 1L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.viewCustomerDetails(customerId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("The customer with customerId " + customerId + " was not found.");
    }

    // Continuation from previous test class setup

    @Test
    void whenTopUpBalanceForExistingCustomer_thenSucceeds() {
        var customerId = 1L;
        var amount = new BigDecimal("50");
        var customer = new Customer(customerId, "John", "Doe", LocalDate.of(1990, 1, 1), "+994501234567",
                BigDecimal.valueOf(100), null, null, 0);
        var updatedCustomer = customer.withBalance(customer.balance().add(amount));
        var customerDTO = new CustomerDTO(customerId, customer.name(), customer.surname(), customer.birthDate(),
                customer.gsmNumber(), updatedCustomer.balance());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);
        when(customerMapper.customerToCustomerDTO(updatedCustomer)).thenReturn(customerDTO);

        var resultDTO = customerService.topUpBalance(customerId, amount);

        assertThat(resultDTO.balance()).isEqualTo(customer.balance().add(amount));
    }

    @Test
    void whenPurchaseWithInsufficientBalance_thenThrows() {
        var customerId = 1L;
        var purchaseAmount = new BigDecimal("150"); // Customer balance is less than this amount
        var customer = new Customer(customerId, "John", "Doe", LocalDate.of(1990, 1, 1), "+994501234567",
                BigDecimal.valueOf(100), null, null, 0);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> customerService.purchase(customerId, purchaseAmount))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void whenRefundForExistingCustomer_thenSucceeds() {
        var customerId = 1L;
        var refundAmount = new BigDecimal("20");
        var customer = new Customer(customerId, "John", "Doe", LocalDate.of(1990, 1, 1), "+994501234567",
                BigDecimal.valueOf(100), null, null, 0);
        var updatedCustomer = customer.withBalance(customer.balance().add(refundAmount));
        var customerDTO = new CustomerDTO(customerId, customer.name(), customer.surname(), customer.birthDate(),
                customer.gsmNumber(), updatedCustomer.balance());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerRepository.save(updatedCustomer)).thenReturn(updatedCustomer);
        when(customerMapper.customerToCustomerDTO(updatedCustomer)).thenReturn(customerDTO);

        var resultDTO = customerService.refund(customerId, refundAmount);

        assertThat(resultDTO.balance()).isEqualTo(customer.balance().add(refundAmount));
    }

    @Test
    void whenAddCustomerSuccessfully_thenReturnsCustomerDTO() {
        var gsmNumber = "+994501234567";
        var customerDTOToCreate =
                new CustomerDTO(null, "John", "Doe", LocalDate.of(1990, 1, 1), gsmNumber, BigDecimal.valueOf(100));
        var customerToSave =
                new Customer(null, "John", "Doe", LocalDate.of(1990, 1, 1), gsmNumber, BigDecimal.valueOf(100), null,
                        null, 0);
        var savedCustomer =
                new Customer(1L, "John", "Doe", LocalDate.of(1990, 1, 1), gsmNumber, BigDecimal.valueOf(100), null,
                        null, 0);
        var expectedDTO =
                new CustomerDTO(1L, "John", "Doe", LocalDate.of(1990, 1, 1), gsmNumber, BigDecimal.valueOf(100));

        when(customerRepository.existsByGsmNumber(gsmNumber)).thenReturn(false);
        when(customerRepository.save(customerToSave)).thenReturn(savedCustomer);
        when(customerMapper.customerToCustomerDTO(savedCustomer)).thenReturn(expectedDTO);

        var resultDTO = customerService.addCustomer(customerDTOToCreate);

        assertThat(resultDTO).isEqualTo(expectedDTO);
        assertThat(resultDTO.id()).isNotNull();
    }

}

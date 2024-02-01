package com.azercell.ms.customer.web;


import com.azercell.ms.customer.domain.CustomerNotFoundException;
import com.azercell.ms.customer.domain.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@WithMockUser(username = "user", password = "password", roles = "USER")
class CustomerControllerMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;


    @Test
    void whenGetCustomerNotExistingThenShouldReturn404() throws Exception {
        Long customerId = 1L;
        given(customerService.viewCustomerDetails(customerId)).willThrow(new CustomerNotFoundException(customerId));
        mockMvc.perform(get("/customers/" + customerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetExistingCustomerThenReturnCustomer() throws Exception {
        Long customerId = 1L;
        CustomerDTO customer = new CustomerDTO(customerId, "John", "Doe", LocalDate.of(1990, 1, 1), "+994501234567",
                BigDecimal.valueOf(100));
        given(customerService.viewCustomerDetails(customerId)).willReturn(customer);

        mockMvc.perform(get("/customers/" + customerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void whenAddCustomerThenReturnCreated() throws Exception {
        CustomerDTO newCustomer = new CustomerDTO(null, "Alice", "Smith", LocalDate.of(1992, 2, 2), "+994501234568",
                BigDecimal.valueOf(100));
        CustomerDTO savedCustomer = new CustomerDTO(2L, "Alice", "Smith", LocalDate.of(1992, 2, 2), "+994501234568",
                BigDecimal.valueOf(100));
        given(customerService.addCustomer(any(CustomerDTO.class))).willReturn(savedCustomer);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"name\":\"Alice\",\"surname\":\"Smith\",\"birthDate\":\"1992-02-02\",\"gsmNumber\":\"+994501234568\",\"balance\":100}"))
                .andExpect(status().isCreated());
    }

    @Test
    void whenInvalidCustomerDataThenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"name\":\"\",\"surname\":\"Smith\",\"birthDate\":\"\",\"gsmNumber\":\"+994\",\"balance\":-100}"))
                .andExpect(status().isBadRequest());
    }

}

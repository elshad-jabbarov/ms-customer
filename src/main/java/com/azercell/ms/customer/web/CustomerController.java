package com.azercell.ms.customer.web;

import com.azercell.ms.customer.domain.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Iterable<CustomerDTO> getCustomers() {
        return customerService.viewCustomerList();
    }

    @GetMapping("{customerId}")
    public CustomerDTO getCustomerById(@PathVariable Long customerId) {
        return customerService.viewCustomerDetails(customerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO addCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        return customerService.addCustomer(customerDTO);
    }

    @PostMapping("{customerId}/topup")
    public CustomerDTO topUpBalance(@PathVariable Long customerId, @RequestBody Amount dto) {
        return customerService.topUpBalance(customerId, dto.amount());
    }

    @PostMapping("{customerId}/purchase")
    public CustomerDTO purchase(@PathVariable Long customerId, @RequestBody Amount dto) {
        return customerService.purchase(customerId, dto.amount());
    }

    @PostMapping("{customerId}/refund")
    public CustomerDTO refund(@PathVariable Long customerId, @RequestBody Amount dto) {
        return customerService.refund(customerId, dto.amount());
    }
}


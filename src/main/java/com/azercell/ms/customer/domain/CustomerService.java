package com.azercell.ms.customer.domain;

import com.azercell.ms.customer.componet.CustomerMapper;
import com.azercell.ms.customer.web.CustomerDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;
    CustomerMapper customerMapper;

    public Iterable<CustomerDTO> viewCustomerList() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                .map(customerMapper::customerToCustomerDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO viewCustomerDetails(Long customerId) {
        return customerMapper.customerToCustomerDTO(getCustomer(customerId));
    }

    @Transactional
    public CustomerDTO topUpBalance(Long customerId, BigDecimal amount) {
        return updateCustomerBalance(customerId, amount);
    }

    @Transactional
    public CustomerDTO purchase(Long customerId, BigDecimal amount) {
        if (getCustomer(customerId).balance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        return updateCustomerBalance(customerId, amount.negate());
    }

    @Transactional
    public CustomerDTO refund(Long customerId, BigDecimal amount) {
        return updateCustomerBalance(customerId, amount);
    }

    private CustomerDTO updateCustomerBalance(Long customerId, BigDecimal amount) {
        Customer customer = getCustomer(customerId);
        Customer updatedCustomer = customer.withBalance(customer.balance().add(amount));
        return customerMapper.customerToCustomerDTO(customerRepository.save(updatedCustomer));
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    public CustomerDTO addCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByGsmNumber(customerDTO.gsmNumber())) {
            throw new CustomerAlreadyExistsException(customerDTO.gsmNumber());
        }
        Customer newCustomer = Customer.of(customerDTO.name(), customerDTO.surname(),
                customerDTO.birthDate(), customerDTO.gsmNumber());
        return customerMapper.customerToCustomerDTO(customerRepository.save(newCustomer));
    }
}

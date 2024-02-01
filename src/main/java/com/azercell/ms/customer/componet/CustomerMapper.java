package com.azercell.ms.customer.componet;

import com.azercell.ms.customer.domain.Customer;
import com.azercell.ms.customer.web.CustomerDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {
    CustomerDTO customerToCustomerDTO(Customer customer);
}


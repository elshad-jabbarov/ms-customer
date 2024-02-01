package com.azercell.ms.customer.domain;


import org.springframework.data.repository.CrudRepository;


public interface CustomerRepository extends CrudRepository<Customer, Long> {

    boolean existsByGsmNumber(String gsmNumber);
}

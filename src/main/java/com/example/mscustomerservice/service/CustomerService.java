package com.example.mscustomerservice.service;

import com.example.mscustomerservice.dto.BusinessCustomerDTO;
import com.example.mscustomerservice.dto.PersonalCustomerDTO;
import com.example.mscustomerservice.model.Customer;
import com.example.mscustomerservice.model.CustomerType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<Customer> findAll();
    Mono<Customer> findById(String id);
    Mono<Customer> findByDocumentNumber(String documentNumber);
    Flux<Customer> findByCustomerType(CustomerType customerType);
    Mono<Customer> createPersonalCustomer(PersonalCustomerDTO customerDTO);
    Mono<Customer> createBusinessCustomer(BusinessCustomerDTO customerDTO);
    Mono<Customer> updatePersonalCustomer(String id, PersonalCustomerDTO customerDTO);
    Mono<Customer> updateBusinessCustomer(String id, BusinessCustomerDTO customerDTO);
    Mono<Void> delete(String id);
}
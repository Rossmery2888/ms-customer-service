package com.example.mscustomerservice.repository;


import com.example.mscustomerservice.model.Customer;
import com.example.mscustomerservice.model.CustomerType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    Mono<Customer> findByDocumentNumber(String documentNumber);
    Flux<Customer> findByCustomerType(CustomerType customerType);
    Mono<Boolean> existsByRuc(String ruc);
    Mono<Boolean> existsByDni(String dni);
}
package com.example.mscustomerservice.repository;

import com.example.mscustomerservice.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    Mono<Customer> findByDocumentNumber(String documentNumber);
    Mono<Boolean> existsByDocumentNumber(String documentNumber);
}

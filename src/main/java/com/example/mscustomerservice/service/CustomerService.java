package com.example.mscustomerservice.service;

import com.example.mscustomerservice.dto.request.CustomerRequestDTO;
import com.example.mscustomerservice.dto.response.CustomerResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Mono<CustomerResponseDTO> createCustomer(CustomerRequestDTO request);
    Mono<CustomerResponseDTO> getCustomerById(String id);
    Flux<CustomerResponseDTO> getAllCustomers();
    Mono<CustomerResponseDTO> updateCustomer(String id, CustomerRequestDTO request);
    Mono<Void> deleteCustomer(String id);
}
package com.example.mscustomerservice.controller;

import com.example.mscustomerservice.dto.BusinessCustomerDTO;
import com.example.mscustomerservice.dto.PersonalCustomerDTO;
import com.example.mscustomerservice.model.Customer;
import com.example.mscustomerservice.model.CustomerType;
import com.example.mscustomerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Flux<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Customer> getCustomerById(@PathVariable String id) {
        return customerService.findById(id);
    }

    @GetMapping("/type/{customerType}")
    public Flux<Customer> getCustomersByType(@PathVariable CustomerType customerType) {
        return customerService.findByCustomerType(customerType);
    }

    @PostMapping("/personal")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> createPersonalCustomer(@Valid @RequestBody PersonalCustomerDTO customerDTO) {
        return customerService.createPersonalCustomer(customerDTO);
    }

    @PostMapping("/business")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> createBusinessCustomer(@Valid @RequestBody BusinessCustomerDTO customerDTO) {
        return customerService.createBusinessCustomer(customerDTO);
    }

    @PutMapping("/personal/{id}")
    public Mono<Customer> updatePersonalCustomer(
            @PathVariable String id,
            @Valid @RequestBody PersonalCustomerDTO customerDTO) {
        return customerService.updatePersonalCustomer(id, customerDTO);
    }

    @PutMapping("/business/{id}")
    public Mono<Customer> updateBusinessCustomer(
            @PathVariable String id,
            @Valid @RequestBody BusinessCustomerDTO customerDTO) {
        return customerService.updateBusinessCustomer(id, customerDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCustomer(@PathVariable String id) {
        return customerService.delete(id);
    }

    @GetMapping("/document/{documentNumber}")
    public Mono<Customer> getCustomerByDocumentNumber(@PathVariable String documentNumber) {
        return customerService.findByDocumentNumber(documentNumber);
    }
}
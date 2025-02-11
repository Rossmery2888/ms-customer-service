package com.example.mscustomerservice.controller;


import com.example.mscustomerservice.dto.request.CustomerRequestDTO;
import com.example.mscustomerservice.dto.response.CustomerResponseDTO;
import com.example.mscustomerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO request) {
        return customerService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public Mono<CustomerResponseDTO> getCustomer(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public Flux<CustomerResponseDTO> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    public Mono<CustomerResponseDTO> updateCustomer(
            @PathVariable String id,
            @RequestBody CustomerRequestDTO request
    ) {
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCustomer(@PathVariable String id) {
        return customerService.deleteCustomer(id);
    }
}
package com.example.mscustomerservice.controller;

import com.example.mscustomerservice.dto.*;
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

    @PostMapping("/personal-vip")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> createPersonalVipCustomer(@Valid @RequestBody PersonalVipCustomerDTO customerDTO) {
        return customerService.createPersonalVipCustomer(customerDTO);
    }

    @PostMapping("/business-pyme")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Customer> createBusinessPymeCustomer(@Valid @RequestBody BusinessPymeCustomerDTO customerDTO) {
        return customerService.createBusinessPymeCustomer(customerDTO);
    }

    @PutMapping("/personal-vip/{id}")
    public Mono<Customer> updatePersonalVipCustomer(
            @PathVariable String id,
            @Valid @RequestBody PersonalVipCustomerDTO customerDTO) {
        return customerService.updatePersonalVipCustomer(id, customerDTO);
    }

    @PutMapping("/business-pyme/{id}")
    public Mono<Customer> updateBusinessPymeCustomer(
            @PathVariable String id,
            @Valid @RequestBody BusinessPymeCustomerDTO customerDTO) {
        return customerService.updateBusinessPymeCustomer(id, customerDTO);
    }
    @GetMapping("/{id}/summary")
    public Mono<CustomerSummaryDTO> getCustomerSummary(@PathVariable String id) {
        return customerService.getCustomerConsolidatedSummary(id);
    }

    @GetMapping("/{id}/debt-status")
    public Mono<DebtStatusDTO> getCustomerDebtStatus(@PathVariable String id) {
        return customerService.validateOverdueDebt(id);
    }

    @GetMapping("/{id}/product-report")
    public Mono<ProductReportDTO> getCustomerProductReport(@PathVariable String id) {
        return customerService.generateProductReport(id);
    }
}

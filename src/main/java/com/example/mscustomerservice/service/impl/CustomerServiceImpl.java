package com.example.mscustomerservice.service.impl;

import com.example.mscustomerservice.dto.request.CustomerRequestDTO;
import com.example.mscustomerservice.dto.response.CustomerResponseDTO;
import com.example.mscustomerservice.model.Customer;
import com.example.mscustomerservice.model.enums.CustomerType;
import com.example.mscustomerservice.repository.CustomerRepository;
import com.example.mscustomerservice.service.CustomerService;
import com.example.mscustomerservice.util.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerValidator validator;

    @Override
    public Mono<CustomerResponseDTO> createCustomer(CustomerRequestDTO request) {
        return validator.validateCustomerRequest(request)
                .flatMap(validRequest -> {
                    Customer customer = mapRequestToCustomer(request);
                    log.info("Creating customer: {}", customer);
                    return customerRepository.save(customer)
                            .map(this::mapCustomerToResponse);
                });
    }

    @Override
    public Mono<CustomerResponseDTO> getCustomerById(String id) {
        return customerRepository.findById(id)
                .map(this::mapCustomerToResponse);
    }

    @Override
    public Flux<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .map(this::mapCustomerToResponse);
    }

    @Override
    public Mono<CustomerResponseDTO> updateCustomer(String id, CustomerRequestDTO request) {
        return validator.validateCustomerRequest(request)
                .flatMap(validRequest -> customerRepository.findById(id)
                        .flatMap(existingCustomer -> {
                            Customer customer = mapRequestToCustomer(request);
                            customer.setId(id);
                            return customerRepository.save(customer);
                        })
                        .map(this::mapCustomerToResponse));
    }

    @Override
    public Mono<Void> deleteCustomer(String id) {
        return customerRepository.deleteById(id);
    }

    // Métodos privados de mapeo
    private Customer mapRequestToCustomer(CustomerRequestDTO request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setType(request.getType());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        if (request.getType() == CustomerType.BUSINESS) {
            customer.setBusinessName(request.getBusinessName());
            customer.setRuc(request.getRuc());
            customer.setBusinessSection(request.getBusinessSection());
        } else {
            customer.setLastName(request.getLastName());
            customer.setDni(request.getDni());
            customer.setOccupation(request.getOccupation());
        }

        return customer;
    }

    private CustomerResponseDTO mapCustomerToResponse(Customer customer) {
        CustomerResponseDTO.CustomerResponseDTOBuilder builder = CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .documentNumber(customer.getDocumentNumber())
                .type(customer.getType())
                .email(customer.getEmail())
                .phone(customer.getPhone());

        if (customer.getType() == CustomerType.BUSINESS) {
            builder.businessName(customer.getBusinessName())
                    .ruc(customer.getRuc())
                    .businessSection(customer.getBusinessSection());
        } else {
            builder.lastName(customer.getLastName())
                    .dni(customer.getDni())
                    .occupation(customer.getOccupation());
        }

        return builder.build();
    }
}
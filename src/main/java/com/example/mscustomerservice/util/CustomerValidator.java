package com.example.mscustomerservice.util;

import com.example.mscustomerservice.dto.request.CustomerRequestDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerValidator {
    public Mono<CustomerRequestDTO> validateCustomerRequest(CustomerRequestDTO request) {
        return Mono.just(request)
                .flatMap(req -> {
                    if (req.getType() == null) {
                        return Mono.error(new IllegalArgumentException("Customer type is required"));
                    }
                    // Más validaciones según tipo de cliente
                    return Mono.just(req);
                });
    }
}

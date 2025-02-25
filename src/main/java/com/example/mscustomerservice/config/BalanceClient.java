package com.example.mscustomerservice.config;

import com.example.mscustomerservice.dto.BalanceDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class BalanceClient {
    private final WebClient webClient;

    public BalanceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-balance-service").build();
    }

    public Mono<BalanceDTO> getCustomerBalance(String customerId) {
        return webClient.get()
                .uri("/balance/customer/{customerId}", customerId)
                .retrieve()
                .bodyToMono(BalanceDTO.class);
    }
}
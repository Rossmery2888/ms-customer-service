package com.example.mscustomerservice.config;

import com.example.mscustomerservice.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class TransactionClient {
    private final WebClient webClient;

    public TransactionClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-transaction-service").build();
    }

    public Flux<TransactionDTO> getRecentTransactions(String customerId, int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/transactions/customer/{customerId}")
                        .queryParam("limit", limit)
                        .build(customerId))
                .retrieve()
                .bodyToFlux(TransactionDTO.class);
    }
}
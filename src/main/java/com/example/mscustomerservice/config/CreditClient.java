package com.example.mscustomerservice.config;

import com.example.mscustomerservice.dto.CreditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CreditClient {
    private final WebClient webClient;

    public CreditClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-credit-service").build();
    }

    public Flux<CreditDTO> getCreditsByCustomerId(String customerId) {
        return webClient.get()
                .uri("/credits/customer/{customerId}", customerId)
                .retrieve()
                .bodyToFlux(CreditDTO.class);
    }

    public Mono<Boolean> hasOverdueCredits(String customerId) {
        return webClient.get()
                .uri("/credits/customer/{customerId}/overdue", customerId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
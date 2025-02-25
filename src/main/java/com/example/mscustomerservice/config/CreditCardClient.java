package com.example.mscustomerservice.config;

import com.example.mscustomerservice.dto.CreditCardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CreditCardClient {
    private final WebClient webClient;

    public CreditCardClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-creditcard-service").build();
    }

    public Flux<CreditCardDTO> getCreditCardsByCustomerId(String customerId) {
        return webClient.get()
                .uri("/creditcards/customer/{customerId}", customerId)
                .retrieve()
                .bodyToFlux(CreditCardDTO.class);
    }

    public Mono<Boolean> hasOverdueCreditCards(String customerId) {
        return webClient.get()
                .uri("/creditcards/customer/{customerId}/overdue", customerId)
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}

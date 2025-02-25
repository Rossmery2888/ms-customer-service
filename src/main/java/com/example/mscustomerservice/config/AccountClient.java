package com.example.mscustomerservice.config;

import com.example.mscustomerservice.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class AccountClient {
    private final WebClient webClient;

    public AccountClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-account-service").build();
    }

    public Flux<AccountDTO> getAccountsByCustomerId(String customerId) {
        return webClient.get()
                .uri("/accounts/customer/{customerId}", customerId)
                .retrieve()
                .bodyToFlux(AccountDTO.class);
    }
}
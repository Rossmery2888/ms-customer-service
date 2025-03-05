package com.nttdata.bankapp.msdebitcardservice.client;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Cliente para el servicio de clientes.
 */
@Component
public class CustomerService {
    private final WebClient webClient;

    public CustomerService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-customer-service").build();
    }

    /**
     * Verifica si un cliente existe.
     * @param customerId ID del cliente
     * @return Mono<Boolean>
     */
    public Mono<Boolean> customerExists(String customerId) {
        return webClient.get()
                .uri("/customers/{id}/exists", customerId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    /**
     * Verifica si un cliente tiene deudas vencidas.
     * @param customerId ID del cliente
     * @return Mono<Boolean>
     */
    public Mono<Boolean> hasOverdueDebts(String customerId) {
        return webClient.get()
                .uri("/customers/{id}/has-overdue-debts", customerId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }
}

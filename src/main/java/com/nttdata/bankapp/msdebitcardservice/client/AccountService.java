package com.nttdata.bankapp.msdebitcardservice.client;

import com.nttdata.bankapp.msdebitcardservice.dto.BalanceDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Cliente para el servicio de cuentas.
 */
@Component
public class AccountService {
    private final WebClient webClient;

    public AccountService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-account-service").build();
    }

    /**
     * Verifica si una cuenta existe.
     * @param accountId ID de la cuenta
     * @return Mono<Boolean>
     */
    public Mono<Boolean> accountExists(String accountId) {
        return webClient.get()
                .uri("/accounts/{id}/exists", accountId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    /**
     * Verifica que una cuenta pertenezca a un cliente.
     * @param accountId ID de la cuenta
     * @param customerId ID del cliente
     * @return Mono<Boolean>
     */
    public Mono<Boolean> verifyAccountOwnership(String accountId, String customerId) {
        return webClient.get()
                .uri("/accounts/{id}/owner/{customerId}", accountId, customerId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }

    /**
     * Obtiene el saldo de una cuenta.
     * @param accountId ID de la cuenta
     * @return Mono<BalanceDto>
     */
    public Mono<BalanceDto> getBalance(String accountId) {
        return webClient.get()
                .uri("/accounts/{id}/balance", accountId)
                .retrieve()
                .bodyToMono(BalanceDto.class);
    }

    /**
     * Actualiza el saldo de una cuenta.
     * @param accountId ID de la cuenta
     * @param amount Monto a actualizar (positivo para depósitos, negativo para retiros)
     * @return Mono<BalanceDto>
     */
    public Mono<BalanceDto> updateBalance(String accountId, BigDecimal amount) {
        return webClient.put()
                .uri("/accounts/{id}/balance", accountId)
                .bodyValue(new BalanceUpdateRequest(amount))
                .retrieve()
                .bodyToMono(BalanceDto.class);
    }

    /**
     * Obtiene el número de cuenta.
     * @param accountId ID de la cuenta
     * @return Mono<String>
     */
    public Mono<String> getAccountNumber(String accountId) {
        return webClient.get()
                .uri("/accounts/{id}/number", accountId)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Data
    @Builder
    public static class BalanceUpdateRequest {
        private BigDecimal amount;

        public BalanceUpdateRequest(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
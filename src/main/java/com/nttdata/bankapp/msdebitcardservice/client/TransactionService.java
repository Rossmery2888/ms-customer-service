package com.nttdata.bankapp.msdebitcardservice.client;

import com.nttdata.bankapp.msdebitcardservice.dto.TransactionDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Cliente para el servicio de transacciones.
 */
@Component
public class TransactionService {
    private final WebClient webClient;

    public TransactionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-transaction-service").build();
    }

    /**
     * Crea una transacción con tarjeta de débito.
     * @param request Datos de la transacción
     * @return Mono<TransactionDto>
     */
    public Mono<TransactionDto> createDebitCardTransaction(DebitCardTransactionRequest request) {
        return webClient.post()
                .uri("/transactions/debit-card")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransactionDto.class);
    }

    /**
     * Obtiene las transacciones de una tarjeta de débito.
     * @param debitCardId ID de la tarjeta
     * @param limit Número máximo de transacciones a obtener
     * @return Flux<TransactionDto>
     */
    public Flux<TransactionDto> getDebitCardTransactions(String debitCardId, int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/transactions/debit-card/{debitCardId}")
                        .queryParam("limit", limit)
                        .build(debitCardId))
                .retrieve()
                .bodyToFlux(TransactionDto.class);
    }

    @Data
    @Builder
    public static class DebitCardTransactionRequest {
        private String debitCardId;
        private String accountId;
        private BigDecimal amount;
        private String description;
        private String customerId;
    }
}
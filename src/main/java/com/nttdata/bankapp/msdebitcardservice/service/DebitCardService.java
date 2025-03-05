package com.nttdata.bankapp.msdebitcardservice.service;

import com.nttdata.bankapp.msdebitcardservice.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Interfaz para el servicio de tarjetas de débito.
 */
public interface DebitCardService {
    Flux<DebitCardDto> findAll();
    Mono<DebitCardDto> findById(String id);
    Flux<DebitCardDto> findByCustomerId(String customerId);
    Mono<DebitCardDto> findByCardNumber(String cardNumber);
    Mono<DebitCardDto> save(DebitCardDto debitCardDto);
    Mono<DebitCardDto> update(String id, DebitCardDto debitCardDto);
    Mono<Void> delete(String id);
    Mono<DebitCardDto> associateAccounts(AccountAssociationRequest request);
    Mono<TransactionDto> processPayment(DebitCardOperationRequest request);
    Mono<CardMovementsReportDto> getLastMovements(String debitCardId, int limit);
    Mono<BalanceDto> getPrimaryAccountBalance(String debitCardId);
}
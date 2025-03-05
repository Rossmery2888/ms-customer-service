package com.nttdata.bankapp.msdebitcardservice.repository;

import com.nttdata.bankapp.msdebitcardservice.model.DebitCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio para entidades DebitCard.
 */
public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard, String> {
    Flux<DebitCard> findByCustomerId(String customerId);
    Mono<DebitCard> findByCardNumber(String cardNumber);
}
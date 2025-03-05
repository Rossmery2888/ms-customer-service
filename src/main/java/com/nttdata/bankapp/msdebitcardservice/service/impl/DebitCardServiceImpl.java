package com.nttdata.bankapp.msdebitcardservice.service.impl;

import com.nttdata.bankapp.msdebitcardservice.client.AccountService;
import com.nttdata.bankapp.msdebitcardservice.client.CustomerService;
import com.nttdata.bankapp.msdebitcardservice.client.TransactionService;
import com.nttdata.bankapp.msdebitcardservice.dto.*;
import com.nttdata.bankapp.msdebitcardservice.exception.DebitCardNotFoundException;
import com.nttdata.bankapp.msdebitcardservice.exception.InsufficientFundsException;
import com.nttdata.bankapp.msdebitcardservice.exception.InvalidOperationException;
import com.nttdata.bankapp.msdebitcardservice.model.DebitCard;
import com.nttdata.bankapp.msdebitcardservice.repository.DebitCardRepository;
import com.nttdata.bankapp.msdebitcardservice.service.DebitCardService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de tarjetas de débito.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DebitCardServiceImpl implements DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @Override
    public Flux<DebitCardDto> findAll() {
        log.info("Finding all debit cards");
        return debitCardRepository.findAll()
                .map(this::mapToDto);
    }

    @Override
    public Mono<DebitCardDto> findById(String id) {
        log.info("Finding debit card by id: {}", id);
        return debitCardRepository.findById(id)
                .map(this::mapToDto)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with id: " + id)));
    }

    @Override
    public Flux<DebitCardDto> findByCustomerId(String customerId) {
        log.info("Finding debit cards by customer id: {}", customerId);
        return debitCardRepository.findByCustomerId(customerId)
                .map(this::mapToDto);
    }

    @Override
    public Mono<DebitCardDto> findByCardNumber(String cardNumber) {
        log.info("Finding debit card by card number: {}", cardNumber);
        return debitCardRepository.findByCardNumber(cardNumber)
                .map(this::mapToDto)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with card number: " + cardNumber)));
    }

    @Override
    @CircuitBreaker(name = "debitCardService", fallbackMethod = "saveFallback")
    @TimeLimiter(name = "debitCardService")
    public Mono<DebitCardDto> save(DebitCardDto debitCardDto) {
        log.info("Saving new debit card: {}", debitCardDto);

        // Verificar si el cliente existe
        return customerService.customerExists(debitCardDto.getCustomerId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new InvalidOperationException("Customer not found with id: " + debitCardDto.getCustomerId()));
                    }

                    // Verificar si el cliente tiene deudas vencidas
                    return customerService.hasOverdueDebts(debitCardDto.getCustomerId())
                            .flatMap(hasDebts -> {
                                if (hasDebts) {
                                    return Mono.error(new InvalidOperationException("Customer has overdue debts and cannot acquire new products"));
                                }

                                // Verificar si existe la cuenta principal
                                return accountService.accountExists(debitCardDto.getPrimaryAccountId())
                                        .flatMap(accountExists -> {
                                            if (!accountExists) {
                                                return Mono.error(new InvalidOperationException("Primary account not found with id: " + debitCardDto.getPrimaryAccountId()));
                                            }

                                            // Verificar que la cuenta pertenezca al cliente
                                            return accountService.verifyAccountOwnership(debitCardDto.getPrimaryAccountId(), debitCardDto.getCustomerId())
                                                    .flatMap(isOwner -> {
                                                        if (!isOwner) {
                                                            return Mono.error(new InvalidOperationException("Primary account does not belong to the customer"));
                                                        }

                                                        DebitCard debitCard = mapToEntity(debitCardDto);

                                                        // Generar número de tarjeta, fecha de expiración y CVV
                                                        debitCard.setCardNumber(generateCardNumber());
                                                        debitCard.setExpirationDate(LocalDate.now().plusYears(4));
                                                        debitCard.setCvv(generateCVV());
                                                        debitCard.setActive(true);
                                                        debitCard.setCreatedAt(LocalDateTime.now());
                                                        debitCard.setUpdatedAt(LocalDateTime.now());

                                                        // Agregar cuenta principal a la lista de cuentas asociadas si no está ya
                                                        if (!debitCard.getAssociatedAccountIds().contains(debitCard.getPrimaryAccountId())) {
                                                            debitCard.getAssociatedAccountIds().add(debitCard.getPrimaryAccountId());
                                                        }

                                                        return debitCardRepository.save(debitCard).map(this::mapToDto);
                                                    });
                                        });
                            });
                });
    }

    // Método de fallback para manejo de errores con Circuit Breaker
    public Mono<DebitCardDto> saveFallback(DebitCardDto debitCardDto, Throwable t) {
        log.error("Fallback for save debit card. Error: {}", t.getMessage());
        return Mono.error(new RuntimeException("Service is currently unavailable. Please try again later."));
    }

    @Override
    @CircuitBreaker(name = "debitCardService", fallbackMethod = "updateFallback")
    @TimeLimiter(name = "debitCardService")
    public Mono<DebitCardDto> update(String id, DebitCardDto debitCardDto) {
        log.info("Updating debit card id: {}", id);
        return debitCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with id: " + id)))
                .flatMap(existingCard -> {
                    // No permitir cambiar campos críticos
                    if (debitCardDto.getCustomerId() != null && !debitCardDto.getCustomerId().equals(existingCard.getCustomerId())) {
                        return Mono.error(new InvalidOperationException("Cannot change debit card owner"));
                    }

                    // Actualizar campos permitidos
                    if (debitCardDto.getActive() != null) {
                        existingCard.setActive(debitCardDto.getActive());
                    }

                    existingCard.setUpdatedAt(LocalDateTime.now());

                    return debitCardRepository.save(existingCard);
                })
                .map(this::mapToDto);
    }

    // Método de fallback para manejo de errores con Circuit Breaker
    public Mono<DebitCardDto> updateFallback(String id, DebitCardDto debitCardDto, Throwable t) {
        log.error("Fallback for update debit card. Error: {}", t.getMessage());
        return Mono.error(new RuntimeException("Service is currently unavailable. Please try again later."));
    }

    @Override
    public Mono<Void> delete(String id) {
        log.info("Deleting debit card id: {}", id);
        return debitCardRepository.findById(id)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with id: " + id)))
                .flatMap(debitCard -> debitCardRepository.deleteById(id));
    }

    @Override
    @CircuitBreaker(name = "debitCardService", fallbackMethod = "associateAccountsFallback")
    @TimeLimiter(name = "debitCardService")
    public Mono<DebitCardDto> associateAccounts(AccountAssociationRequest request) {
        log.info("Associating accounts to debit card: {}", request);

        return debitCardRepository.findById(request.getDebitCardId())
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with id: " + request.getDebitCardId())))
                .flatMap(debitCard -> {
                    // Verificar que la tarjeta pertenezca al cliente
                    if (!debitCard.getCustomerId().equals(request.getCustomerId())) {
                        return Mono.error(new InvalidOperationException("Debit card does not belong to the customer"));
                    }

                    // Verificar si la cuenta principal existe
                    return accountService.accountExists(request.getPrimaryAccountId())
                            .flatMap(primaryExists -> {
                                if (!primaryExists) {
                                    return Mono.error(new InvalidOperationException("Primary account not found with id: " + request.getPrimaryAccountId()));
                                }

                                // Verificar que la cuenta principal pertenezca al cliente
                                return accountService.verifyAccountOwnership(request.getPrimaryAccountId(), request.getCustomerId())
                                        .flatMap(isOwner -> {
                                            if (!isOwner) {
                                                return Mono.error(new InvalidOperationException("Primary account does not belong to the customer"));
                                            }

                                            // Verificar que todas las cuentas asociadas existan y pertenezcan al cliente
                                            List<Mono<Boolean>> ownershipChecks = request.getAssociatedAccountIds().stream()
                                                    .map(accountId -> accountService.verifyAccountOwnership(accountId, request.getCustomerId())
                                                            .flatMap(belongsToCustomer -> {
                                                                if (!belongsToCustomer) {
                                                                    return Mono.error(new InvalidOperationException("Account with id " + accountId + " does not belong to the customer"));
                                                                }
                                                                return Mono.just(true);
                                                            }))
                                                    .collect(Collectors.toList());

                                            return Flux.merge(ownershipChecks)
                                                    .collectList()
                                                    .flatMap(results -> {
                                                        // Actualizar la tarjeta con la nueva cuenta principal y cuentas asociadas
                                                        debitCard.setPrimaryAccountId(request.getPrimaryAccountId());

                                                        // Asegurar que la cuenta principal esté en la lista de cuentas asociadas
                                                        List<String> uniqueAccountIds = new ArrayList<>(request.getAssociatedAccountIds());
                                                        if (!uniqueAccountIds.contains(request.getPrimaryAccountId())) {
                                                            uniqueAccountIds.add(0, request.getPrimaryAccountId());
                                                        } else {
                                                            // Si ya está en la lista, moverla al inicio
                                                            uniqueAccountIds.remove(request.getPrimaryAccountId());
                                                            uniqueAccountIds.add(0, request.getPrimaryAccountId());
                                                        }

                                                        // Eliminar duplicados manteniendo el orden
                                                        debitCard.setAssociatedAccountIds(new ArrayList<>(
                                                                new LinkedHashSet<>(uniqueAccountIds)));

                                                        debitCard.setUpdatedAt(LocalDateTime.now());

                                                        return debitCardRepository.save(debitCard);
                                                    });
                                        });
                            });
                })
                .map(this::mapToDto);
    }

    // Método de fallback para manejo de errores con Circuit Breaker
    public Mono<DebitCardDto> associateAccountsFallback(AccountAssociationRequest request, Throwable t) {
        log.error("Fallback for associate accounts. Error: {}", t.getMessage());
        return Mono.error(new RuntimeException("Service is currently unavailable. Please try again later."));
    }

    @Override
    @CircuitBreaker(name = "debitCardService", fallbackMethod = "processPaymentFallback")
    @TimeLimiter(name = "debitCardService")
    public Mono<TransactionDto> processPayment(DebitCardOperationRequest request) {
        log.info("Processing payment with debit card: {}", request);

        return debitCardRepository.findById(request.getDebitCardId())
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with id: " + request.getDebitCardId())))
                .flatMap(debitCard -> {
                    // Verificar que la tarjeta pertenezca al cliente
                    if (!debitCard.getCustomerId().equals(request.getCustomerId())) {
                        return Mono.error(new InvalidOperationException("Debit card does not belong to the customer"));
                    }

                    // Verificar que la tarjeta esté activa
                    if (!debitCard.getActive()) {
                        return Mono.error(new InvalidOperationException("Debit card is not active"));
                    }

                    // Obtener la lista ordenada de cuentas para intentar el pago
                    List<String> accountsToTry = new ArrayList<>(debitCard.getAssociatedAccountIds());

                    // Función recursiva para intentar el pago en cada cuenta hasta que alguna tenga fondos suficientes
                    return tryPaymentOnAccounts(accountsToTry, request.getAmount(), 0)
                            .flatMap(successfulAccountId -> {
                                // Crear la transacción con la cuenta que fue exitosa
                                return transactionService.createDebitCardTransaction(
                                        TransactionService.DebitCardTransactionRequest.builder()
                                                .debitCardId(debitCard.getId())
                                                .accountId(successfulAccountId)
                                                .amount(request.getAmount())
                                                .description(request.getDescription())
                                                .customerId(request.getCustomerId())
                                                .build());
                            });
                });
    }

    /**
     * Intenta realizar un pago en las cuentas asociadas a una tarjeta de débito,
     * en el orden en que se encuentran.
     *
     * @param accountIds Lista de IDs de cuentas a intentar
     * @param amount Monto del pago
     * @param currentIndex Índice actual en la lista de cuentas
     * @return Mono con el ID de la cuenta donde se realizó el pago exitosamente
     */
    private Mono<String> tryPaymentOnAccounts(List<String> accountIds, BigDecimal amount, int currentIndex) {
        // Si ya no hay más cuentas, no hay fondos suficientes en ninguna
        if (currentIndex >= accountIds.size()) {
            return Mono.error(new InsufficientFundsException("Insufficient funds in all associated accounts"));
        }

        String accountId = accountIds.get(currentIndex);

        // Intentar retirar de esta cuenta
        return accountService.getBalance(accountId)
                .flatMap(balance -> {
                    if (balance.getBalance().compareTo(amount) >= 0) {
                        // Esta cuenta tiene fondos suficientes, realizar retiro
                        return accountService.updateBalance(accountId, amount.negate())
                                .thenReturn(accountId);
                    } else {
                        // Esta cuenta no tiene fondos suficientes, probar con la siguiente
                        return tryPaymentOnAccounts(accountIds, amount, currentIndex + 1);
                    }
                });
    }

    // Método de fallback para manejo de errores con Circuit Breaker
    public Mono<TransactionDto> processPaymentFallback(DebitCardOperationRequest request, Throwable t) {
        log.error("Fallback for process payment. Error: {}", t.getMessage());
        return Mono.error(new RuntimeException("Service is currently unavailable. Please try again later."));
    }

    @Override
    @CircuitBreaker(name = "debitCardService", fallbackMethod = "getLastMovementsFallback")
    @TimeLimiter(name = "debitCardService")
    public Mono<CardMovementsReportDto> getLastMovements(String debitCardId, int limit) {
        log.info("Getting last {} movements for debit card id: {}", limit, debitCardId);

        return debitCardRepository.findById(debitCardId)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with id: " + debitCardId)))
                .flatMap(debitCard -> {
                    // Obtener las últimas transacciones de la tarjeta de débito
                    return transactionService.getDebitCardTransactions(debitCardId, limit)
                            .collectList()
                            .flatMap(transactions -> {
                                // Convertir transacciones a movimientos
                                Flux<MovementDto> movementFlux = Flux.fromIterable(transactions)
                                        .flatMap(transaction -> {
                                            // Para cada transacción, obtener el número de cuenta
                                            return accountService.getAccountNumber(transaction.getAccountId())
                                                    .map(accountNumber -> MovementDto.builder()
                                                            .transactionId(transaction.getId())
                                                            .transactionType(transaction.getType().toString())
                                                            .transactionDate(transaction.getTransactionDate())
                                                            .description(transaction.getDescription())
                                                            .amount(transaction.getAmount())
                                                            .accountNumber(accountNumber)
                                                            .build());
                                        });

                                return movementFlux.collectList()
                                        .map(movements -> CardMovementsReportDto.builder()
                                                .cardId(debitCard.getId())
                                                .cardNumber(debitCard.getCardNumber())
                                                .cardType("DEBIT_CARD")
                                                .movements(movements)
                                                .build());
                            });
                });
    }

    // Método de fallback para manejo de errores con Circuit Breaker
    public Mono<CardMovementsReportDto> getLastMovementsFallback(String debitCardId, int limit, Throwable t) {
        log.error("Fallback for get last movements. Error: {}", t.getMessage());
        return Mono.error(new RuntimeException("Service is currently unavailable. Please try again later."));
    }

    @Override
    @CircuitBreaker(name = "debitCardService", fallbackMethod = "getPrimaryAccountBalanceFallback")
    @TimeLimiter(name = "debitCardService")
    public Mono<BalanceDto> getPrimaryAccountBalance(String debitCardId) {
        log.info("Getting primary account balance for debit card id: {}", debitCardId);

        return debitCardRepository.findById(debitCardId)
                .switchIfEmpty(Mono.error(new DebitCardNotFoundException("Debit card not found with id: " + debitCardId)))
                .flatMap(debitCard -> {
                    // Obtener el saldo de la cuenta principal
                    return accountService.getBalance(debitCard.getPrimaryAccountId());
                });
    }

    // Método de fallback para manejo de errores con Circuit Breaker
    public Mono<BalanceDto> getPrimaryAccountBalanceFallback(String debitCardId, Throwable t) {
        log.error("Fallback for get primary account balance. Error: {}", t.getMessage());
        return Mono.error(new RuntimeException("Service is currently unavailable. Please try again later."));
    }

    /**
     * Genera un número de tarjeta de débito aleatorio.
     * @return String con el número de tarjeta
     */
    private String generateCardNumber() {
        // Formato: XXXX-XXXX-XXXX-XXXX
        return String.format("4%03d-%04d-%04d-%04d",
                new Random().nextInt(1000),
                new Random().nextInt(10000),
                new Random().nextInt(10000),
                new Random().nextInt(10000));
    }

    /**
     * Genera un código CVV aleatorio.
     * @return String con el código CVV
     */
    private String generateCVV() {
        return String.format("%03d", new Random().nextInt(1000));
    }

    /**
     * Convierte una entidad DebitCard a DTO.
     * @param debitCard Entidad a convertir
     * @return DebitCardDto
     */
    private DebitCardDto mapToDto(DebitCard debitCard) {
        return DebitCardDto.builder()
                .id(debitCard.getId())
                .cardNumber(debitCard.getCardNumber())
                .customerId(debitCard.getCustomerId())
                .primaryAccountId(debitCard.getPrimaryAccountId())
                .associatedAccountIds(debitCard.getAssociatedAccountIds())
                .expirationDate(debitCard.getExpirationDate())
                .cvv(debitCard.getCvv())
                .active(debitCard.getActive())
                .build();
    }

    /**
     * Convierte un DTO a entidad DebitCard.
     * @param debitCardDto DTO a convertir
     * @return DebitCard
     */
    private DebitCard mapToEntity(DebitCardDto debitCardDto) {
        return DebitCard.builder()
                .customerId(debitCardDto.getCustomerId())
                .primaryAccountId(debitCardDto.getPrimaryAccountId())
                .associatedAccountIds(debitCardDto.getAssociatedAccountIds() != null
                        ? new ArrayList<>(debitCardDto.getAssociatedAccountIds()) : new ArrayList<>())
                .active(debitCardDto.getActive())
                .build();
    }
}

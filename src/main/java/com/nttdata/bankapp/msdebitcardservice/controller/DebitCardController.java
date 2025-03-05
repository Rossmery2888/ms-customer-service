package com.nttdata.bankapp.msdebitcardservice.controller;

import com.nttdata.bankapp.msdebitcardservice.dto.*;
import com.nttdata.bankapp.msdebitcardservice.service.DebitCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * Controlador reactivo para operaciones con tarjetas de débito.
 */
@RestController
@RequestMapping("/debit-cards")
@RequiredArgsConstructor
@Slf4j
public class DebitCardController {

    private final DebitCardService debitCardService;

    /**
     * Obtiene todas las tarjetas de débito.
     * @return Flux de DebitCardDto
     */
    @GetMapping
    public Flux<DebitCardDto> getAll() {
        log.info("GET /debit-cards");
        return debitCardService.findAll();
    }

    /**
     * Obtiene una tarjeta de débito por su ID.
     * @param id ID de la tarjeta de débito
     * @return Mono de ResponseEntity con DebitCardDto
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DebitCardDto>> getById(@PathVariable String id) {
        log.info("GET /debit-cards/{}", id);
        return debitCardService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene las tarjetas de débito de un cliente.
     * @param customerId ID del cliente
     * @return Flux de DebitCardDto
     */
    @GetMapping("/customer/{customerId}")
    public Flux<DebitCardDto> getByCustomerId(@PathVariable String customerId) {
        log.info("GET /debit-cards/customer/{}", customerId);
        return debitCardService.findByCustomerId(customerId);
    }

    /**
     * Obtiene una tarjeta de débito por su número.
     * @param cardNumber Número de tarjeta
     * @return Mono de ResponseEntity con DebitCardDto
     */
    @GetMapping("/number/{cardNumber}")
    public Mono<ResponseEntity<DebitCardDto>> getByCardNumber(@PathVariable String cardNumber) {
        log.info("GET /debit-cards/number/{}", cardNumber);
        return debitCardService.findByCardNumber(cardNumber)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Crea una nueva tarjeta de débito.
     * @param debitCardDto DTO con los datos de la tarjeta
     * @return Mono de DebitCardDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DebitCardDto> create(@Valid @RequestBody DebitCardDto debitCardDto) {
        log.info("POST /debit-cards");
        return debitCardService.save(debitCardDto);
    }

    /**
     * Actualiza una tarjeta de débito existente.
     * @param id ID de la tarjeta
     * @param debitCardDto DTO con los datos a actualizar
     * @return Mono de ResponseEntity con DebitCardDto
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DebitCardDto>> update(
            @PathVariable String id,
            @Valid @RequestBody DebitCardDto debitCardDto) {
        log.info("PUT /debit-cards/{}", id);
        return debitCardService.update(id, debitCardDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Elimina una tarjeta de débito.
     * @param id ID de la tarjeta
     * @return Mono<Void>
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        log.info("DELETE /debit-cards/{}", id);
        return debitCardService.delete(id);
    }

    /**
     * Asocia cuentas a una tarjeta de débito.
     * @param request DTO con los datos de la asociación
     * @return Mono de DebitCardDto
     */
    @PutMapping("/associate-accounts")
    public Mono<DebitCardDto> associateAccounts(@Valid @RequestBody AccountAssociationRequest request) {
        log.info("PUT /debit-cards/associate-accounts");
        return debitCardService.associateAccounts(request);
    }

    /**
     * Procesa un pago con tarjeta de débito.
     * @param request DTO con los datos de la operación
     * @return Mono de TransactionDto
     */
    @PostMapping("/process-payment")
    public Mono<TransactionDto> processPayment(@Valid @RequestBody DebitCardOperationRequest request) {
        log.info("POST /debit-cards/process-payment");
        return debitCardService.processPayment(request);
    }

    /**
     * Obtiene los últimos movimientos de una tarjeta de débito.
     * @param id ID de la tarjeta
     * @param limit Número de movimientos a obtener (por defecto 10)
     * @return Mono de CardMovementsReportDto
     */
    @GetMapping("/{id}/movements")
    public Mono<CardMovementsReportDto> getLastMovements(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        log.info("GET /debit-cards/{}/movements with limit: {}", id, limit);
        return debitCardService.getLastMovements(id, limit);
    }

    /**
     * Consulta el saldo de la cuenta principal de una tarjeta de débito.
     * @param id ID de la tarjeta
     * @return Mono de BalanceDto
     */
    @GetMapping("/{id}/balance")
    public Mono<BalanceDto> getPrimaryAccountBalance(@PathVariable String id) {
        log.info("GET /debit-cards/{}/balance", id);
        return debitCardService.getPrimaryAccountBalance(id);
    }
}
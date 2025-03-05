package com.nttdata.bankapp.msdebitcardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para un movimiento individual de tarjeta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementDto {
    private String transactionId;
    private String transactionType;
    private LocalDateTime transactionDate;
    private String description;
    private BigDecimal amount;
    private String accountNumber; // Número de cuenta afectada (para débito)
}
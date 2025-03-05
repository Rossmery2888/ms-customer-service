package com.nttdata.bankapp.msdebitcardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para reporte de últimos movimientos de tarjeta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardMovementsReportDto {
    private String cardId;
    private String cardNumber;
    private String cardType; // DEBIT_CARD, CREDIT_CARD
    private List<MovementDto> movements;
}
package com.nttdata.bankapp.msdebitcardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para representar el saldo de una cuenta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDto {
    private String accountId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
}
package com.nttdata.bankapp.msdebitcardservice.dto;
import com.nttdata.bankapp.msdebitcardservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para representar una transacción.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private String id;
    private String accountId;
    private String debitCardId;
    private String customerId;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
    private TransactionType type;

}
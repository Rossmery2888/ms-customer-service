package com.nttdata.bankapp.msdebitcardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para realizar operación con tarjeta de débito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitCardOperationRequest {
    @NotBlank(message = "Debit card ID is required")
    private String debitCardId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "Customer ID is required")
    private String customerId;
}

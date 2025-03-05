package com.nttdata.bankapp.msdebitcardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO para asociar cuentas a tarjeta de débito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountAssociationRequest {
    @NotBlank(message = "Debit card ID is required")
    private String debitCardId;

    @NotBlank(message = "Primary account ID is required")
    private String primaryAccountId;

    @NotEmpty(message = "Associated accounts list cannot be empty")
    private List<String> associatedAccountIds;

    @NotBlank(message = "Customer ID is required")
    private String customerId;
}
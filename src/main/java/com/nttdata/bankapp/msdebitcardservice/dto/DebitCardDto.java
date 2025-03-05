package com.nttdata.bankapp.msdebitcardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para representar una tarjeta de débito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitCardDto {
    private String id;
    private String cardNumber;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Primary account ID is required")
    private String primaryAccountId;

    private List<String> associatedAccountIds;
    private LocalDate expirationDate;
    private String cvv;
    private Boolean active;
}
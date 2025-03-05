package com.nttdata.bankapp.msdebitcardservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para representar una tarjeta de débito.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "debitCards")
public class DebitCard {
    @Id
    private String id;

    @Indexed(unique = true)
    private String cardNumber;
    private String customerId;
    private String primaryAccountId; // ID de la cuenta principal
    private List<String> associatedAccountIds = new ArrayList<>(); // IDs de cuentas asociadas adicionales
    private LocalDate expirationDate;
    private String cvv;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

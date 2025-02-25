package com.example.mscustomerservice.dto;

import lombok.Data;

@Data
public class CreditCardDTO {
    private String cardId;
    private String cardNumber;
    private Double creditLimit;
    private Double currentBalance;
    private Boolean isOverdue;
}
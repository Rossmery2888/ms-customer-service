package com.example.mscustomerservice.dto;

import lombok.Data;

@Data
public class TransactionDTO {
    private String transactionId;
    private String productId;
    private String productType;
    private Double amount;
    private String date;
    private String description;
}

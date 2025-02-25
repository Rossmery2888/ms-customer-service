package com.example.mscustomerservice.dto;

import lombok.Data;

@Data
public class CreditDTO {
    private String creditId;
    private String creditNumber;
    private Double amount;
    private Double outstandingDebt;
    private Boolean isOverdue;
}
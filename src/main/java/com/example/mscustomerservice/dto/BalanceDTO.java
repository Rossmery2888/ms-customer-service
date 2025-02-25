package com.example.mscustomerservice.dto;

import lombok.Data;

@Data
public class BalanceDTO {
    private Double totalBalance;
    private Double totalDebt;
    private Double availableCredit;
}
package com.example.mscustomerservice.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private String accountId;
    private String accountNumber;
    private String accountType;
    private Double balance;
}
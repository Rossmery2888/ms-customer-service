package com.example.mscustomerservice.dto;

import com.example.mscustomerservice.model.CustomerType;
import lombok.Data;

import java.util.List;

@Data
public class CustomerSummaryDTO {
    private String customerId;
    private String documentNumber;
    private String customerName;
    private CustomerType customerType;
    private List<AccountDTO> accounts;
    private List<CreditDTO> credits;
    private List<CreditCardDTO> creditCards;
    private BalanceDTO balance;
    private List<TransactionDTO> recentTransactions;
}
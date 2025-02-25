package com.example.mscustomerservice.dto;

import com.example.mscustomerservice.model.CustomerType;
import lombok.Data;

import java.util.List;

@Data
public class ProductReportDTO {
    private String customerId;
    private String documentNumber;
    private String customerName;
    private CustomerType customerType;
    private Integer totalAccounts;
    private Integer totalCredits;
    private Integer totalCreditCards;
    private Double totalDeposits;
    private Double totalLoans;
    private Boolean hasOverdueDebt;
    private List<AccountDTO> accounts;
    private List<CreditDTO> credits;
    private List<CreditCardDTO> creditCards;
}

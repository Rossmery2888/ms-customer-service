package com.example.mscustomerservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class DebtStatusDTO {
    private Boolean hasOverdueDebt;
    private Double totalOverdueAmount;
    private List<OverdueProductDTO> overdueProducts;
}
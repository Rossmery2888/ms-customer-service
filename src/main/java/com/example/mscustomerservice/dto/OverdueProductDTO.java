package com.example.mscustomerservice.dto;

import lombok.Data;

@Data
public class OverdueProductDTO {
    private String productId;
    private String productType;
    private Double overdueAmount;
    private String dueDate;
}
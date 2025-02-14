package com.example.mscustomerservice.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class BusinessCustomerDTO {
    private String id;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "RUC is required")
    @Pattern(regexp = "\\d{11}", message = "RUC must be 11 digits")
    private String ruc;

    @NotBlank(message = "Business type is required")
    private String businessType;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{9}", message = "Phone must be 9 digits")
    private String phone;
}

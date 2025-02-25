package com.example.mscustomerservice.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class PersonalVipCustomerDTO {
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "DNI is required")
    @Pattern(regexp = "\\d{8}", message = "DNI must be 8 digits")
    private String dni;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{9}", message = "Phone must be 9 digits")
    private String phone;

    @NotNull(message = "Credit card status is required")
    private Boolean hasCreditCard;

    @NotNull(message = "Monthly average balance is required")
    @Min(value = 0, message = "Monthly average balance must be positive")
    private Double monthlyAverageBalance;
}
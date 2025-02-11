package com.example.mscustomerservice.dto.request;

import com.example.mscustomerservice.model.enums.CustomerType;
import lombok.Data;

@Data
public class CustomerRequestDTO {
    // Datos comunes
    private String name;
    private String documentNumber;
    private CustomerType type;
    private String email;
    private String phone;

    // Datos empresariales
    private String businessName;
    private String ruc;
    private String businessSection;

    // Datos personales
    private String lastName;
    private String dni;
    private String occupation;
}
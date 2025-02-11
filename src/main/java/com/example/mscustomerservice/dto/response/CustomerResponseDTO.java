package com.example.mscustomerservice.dto.response;

import com.example.mscustomerservice.model.enums.CustomerType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)  // Esta anotación excluirá los campos null
public class CustomerResponseDTO {
    private String id;
    private String name;
    private String documentNumber;
    private CustomerType type;
    private String email;
    private String phone;

    // Campos para cliente empresarial
    private String businessName;
    private String ruc;
    private String businessSection;

    // Campos para cliente personal
    private String lastName;
    private String dni;
    private String occupation;
}
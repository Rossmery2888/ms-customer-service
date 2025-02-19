package com.example.mscustomerservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "customers")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer {
    @Id
    private String id;
    private CustomerType customerType;
    private String documentNumber;
    private String email;
    private String phone;

    // Campos específicos para clientes personales
    private String firstName;
    private String lastName;
    private String dni;

    // Campos específicos para clientes empresariales
    private String businessName;
    private String ruc;
    private String businessType;

    // VIP and PYME
    private Boolean hasCreditCard;
    private Double monthlyAverageBalance;
}
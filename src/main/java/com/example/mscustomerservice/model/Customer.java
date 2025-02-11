package com.example.mscustomerservice.model;

import com.example.mscustomerservice.model.enums.CustomerType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;

    // Datos comunes
    private String name;
    @Indexed(unique = true)
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
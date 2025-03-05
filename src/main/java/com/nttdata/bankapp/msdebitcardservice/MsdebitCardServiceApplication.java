package com.nttdata.bankapp.msdebitcardservice;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Clase principal de la aplicación.
 */
@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(
        title = "Credit Card Service API",
        version = "1.0",
        description = "API para la gestión de tarjetas de crédito"
))
public class MsdebitCardServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsdebitCardServiceApplication.class, args);
    }
}
package com.example.mscustomerservice.service.impl;


import com.example.mscustomerservice.config.*;
import com.example.mscustomerservice.dto.*;
import com.example.mscustomerservice.exception.CustomerNotFoundException;
import com.example.mscustomerservice.model.Customer;
import com.example.mscustomerservice.model.CustomerType;
import com.example.mscustomerservice.repository.CustomerRepository;
import com.example.mscustomerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountClient accountClient;
    private final CreditClient creditClient;
    private final CreditCardClient creditCardClient;
    private final BalanceClient balanceClient;
    private final TransactionClient transactionClient;

    @Override
    public Flux<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Mono<Customer> findById(String id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)));
    }

    @Override
    public Mono<Customer> findByDocumentNumber(String documentNumber) {
        return customerRepository.findByDocumentNumber(documentNumber)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Document number: " + documentNumber)));
    }

    @Override
    public Flux<Customer> findByCustomerType(CustomerType customerType) {
        return customerRepository.findByCustomerType(customerType);
    }

    @Override
    public Mono<Customer> createPersonalCustomer(PersonalCustomerDTO dto) {
        return customerRepository.existsByDni(dto.getDni())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("DNI already exists"));
                    }

                    Customer customer = new Customer();
                    customer.setCustomerType(CustomerType.PERSONAL);
                    customer.setFirstName(dto.getFirstName());
                    customer.setLastName(dto.getLastName());
                    customer.setDni(dto.getDni());
                    customer.setDocumentNumber(dto.getDni());
                    customer.setEmail(dto.getEmail());
                    customer.setPhone(dto.getPhone());

                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<Customer> createBusinessCustomer(BusinessCustomerDTO dto) {
        return customerRepository.existsByRuc(dto.getRuc())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("RUC already exists"));
                    }

                    Customer customer = new Customer();
                    customer.setCustomerType(CustomerType.BUSINESS);
                    customer.setBusinessName(dto.getBusinessName());
                    customer.setRuc(dto.getRuc());
                    customer.setDocumentNumber(dto.getRuc());
                    customer.setBusinessType(dto.getBusinessType());
                    customer.setEmail(dto.getEmail());
                    customer.setPhone(dto.getPhone());

                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<Customer> updatePersonalCustomer(String id, PersonalCustomerDTO dto) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(existingCustomer -> {
                    if (existingCustomer.getCustomerType() != CustomerType.PERSONAL) {
                        return Mono.error(new IllegalArgumentException("Customer is not a personal customer"));
                    }

                    // Verificar si el nuevo DNI ya existe y es diferente al actual
                    if (!existingCustomer.getDni().equals(dto.getDni())) {
                        return customerRepository.existsByDni(dto.getDni())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new IllegalArgumentException("DNI already exists"));
                                    }
                                    return updatePersonalCustomerFields(existingCustomer, dto);
                                });
                    }

                    return updatePersonalCustomerFields(existingCustomer, dto);
                });
    }

    private Mono<Customer> updatePersonalCustomerFields(Customer customer, PersonalCustomerDTO dto) {
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setDni(dto.getDni());
        customer.setDocumentNumber(dto.getDni());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        return customerRepository.save(customer);
    }

    @Override
    public Mono<Customer> updateBusinessCustomer(String id, BusinessCustomerDTO dto) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(existingCustomer -> {
                    if (existingCustomer.getCustomerType() != CustomerType.BUSINESS) {
                        return Mono.error(new IllegalArgumentException("Customer is not a business customer"));
                    }

                    // Verificar si el nuevo RUC ya existe y es diferente al actual
                    if (!existingCustomer.getRuc().equals(dto.getRuc())) {
                        return customerRepository.existsByRuc(dto.getRuc())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new IllegalArgumentException("RUC already exists"));
                                    }
                                    return updateBusinessCustomerFields(existingCustomer, dto);
                                });
                    }

                    return updateBusinessCustomerFields(existingCustomer, dto);
                });
    }

    private Mono<Customer> updateBusinessCustomerFields(Customer customer, BusinessCustomerDTO dto) {
        customer.setBusinessName(dto.getBusinessName());
        customer.setRuc(dto.getRuc());
        customer.setDocumentNumber(dto.getRuc());
        customer.setBusinessType(dto.getBusinessType());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        return customerRepository.save(customer);
    }

    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(customer -> customerRepository.deleteById(id));
    }

    @Override
    public Mono<Customer> createPersonalVipCustomer(PersonalVipCustomerDTO dto) {
        return customerRepository.existsByDni(dto.getDni())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("DNI already exists"));
                    }

                    if (!dto.getHasCreditCard()) {
                        return Mono.error(new IllegalArgumentException("VIP customers must have a credit card"));
                    }

                    if (dto.getMonthlyAverageBalance() < 1000) { // Example minimum balance
                        return Mono.error(new IllegalArgumentException("VIP customers must maintain a minimum monthly average balance"));
                    }

                    Customer customer = new Customer();
                    customer.setCustomerType(CustomerType.PERSONAL_VIP);
                    customer.setFirstName(dto.getFirstName());
                    customer.setLastName(dto.getLastName());
                    customer.setDni(dto.getDni());
                    customer.setDocumentNumber(dto.getDni());
                    customer.setEmail(dto.getEmail());
                    customer.setPhone(dto.getPhone());
                    customer.setHasCreditCard(dto.getHasCreditCard());
                    customer.setMonthlyAverageBalance(dto.getMonthlyAverageBalance());

                    return customerRepository.save(customer);
                });
    }

    @Override
    public Mono<Customer> createBusinessPymeCustomer(BusinessPymeCustomerDTO dto) {
        return customerRepository.existsByRuc(dto.getRuc())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("RUC already exists"));
                    }

                    if (!dto.getHasCreditCard()) {
                        return Mono.error(new IllegalArgumentException("PYME customers must have a credit card"));
                    }

                    Customer customer = new Customer();
                    customer.setCustomerType(CustomerType.BUSINESS_PYME);
                    customer.setBusinessName(dto.getBusinessName());
                    customer.setRuc(dto.getRuc());
                    customer.setDocumentNumber(dto.getRuc());
                    customer.setBusinessType(dto.getBusinessType());
                    customer.setEmail(dto.getEmail());
                    customer.setPhone(dto.getPhone());
                    customer.setHasCreditCard(dto.getHasCreditCard());

                    return customerRepository.save(customer);
                });
    }


    @Override
    public Mono<Customer> updatePersonalVipCustomer(String id, PersonalVipCustomerDTO dto) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(existingCustomer -> {
                    if (existingCustomer.getCustomerType() != CustomerType.PERSONAL_VIP) {
                        return Mono.error(new IllegalArgumentException("Customer is not a VIP customer"));
                    }

                    if (!dto.getHasCreditCard()) {
                        return Mono.error(new IllegalArgumentException("VIP customers must have a credit card"));
                    }

                    if (dto.getMonthlyAverageBalance() < 1000) {
                        return Mono.error(new IllegalArgumentException("VIP customers must maintain a minimum monthly average balance"));
                    }

                    return updatePersonalVipCustomerFields(existingCustomer, dto);
                });
    }

    private Mono<Customer> updatePersonalVipCustomerFields(Customer customer, PersonalVipCustomerDTO dto) {
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setDni(dto.getDni());
        customer.setDocumentNumber(dto.getDni());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setHasCreditCard(dto.getHasCreditCard());
        customer.setMonthlyAverageBalance(dto.getMonthlyAverageBalance());
        return customerRepository.save(customer);
    }

    @Override
    public Mono<Customer> updateBusinessPymeCustomer(String id, BusinessPymeCustomerDTO dto) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMap(existingCustomer -> {
                    if (existingCustomer.getCustomerType() != CustomerType.BUSINESS_PYME) {
                        return Mono.error(new IllegalArgumentException("Customer is not a PYME customer"));
                    }

                    if (!dto.getHasCreditCard()) {
                        return Mono.error(new IllegalArgumentException("PYME customers must have a credit card"));
                    }

                    return updateBusinessPymeCustomerFields(existingCustomer, dto);
                });
    }

    private Mono<Customer> updateBusinessPymeCustomerFields(Customer customer, BusinessPymeCustomerDTO dto) {
        customer.setBusinessName(dto.getBusinessName());
        customer.setRuc(dto.getRuc());
        customer.setDocumentNumber(dto.getRuc());
        customer.setBusinessType(dto.getBusinessType());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setHasCreditCard(dto.getHasCreditCard());
        return customerRepository.save(customer);
    }
    @Override
    public Mono<CustomerSummaryDTO> getCustomerConsolidatedSummary(String customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .flatMap(customer -> {
                    CustomerSummaryDTO summary = new CustomerSummaryDTO();
                    summary.setCustomerId(customer.getId());
                    summary.setDocumentNumber(customer.getDocumentNumber());
                    summary.setCustomerType(customer.getCustomerType());

                    // Establecer el nombre según el tipo de cliente
                    if (customer.getCustomerType() == CustomerType.PERSONAL
                            || customer.getCustomerType() == CustomerType.PERSONAL_VIP) {
                        summary.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
                    } else {
                        summary.setCustomerName(customer.getBusinessName());
                    }

                    // Obtener información de cuentas
                    Mono<List<AccountDTO>> accountsMono = accountClient.getAccountsByCustomerId(customerId)
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    // Obtener información de créditos
                    Mono<List<CreditDTO>> creditsMono = creditClient.getCreditsByCustomerId(customerId)
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    // Obtener información de tarjetas de crédito
                    Mono<List<CreditCardDTO>> creditCardsMono = creditCardClient.getCreditCardsByCustomerId(customerId)
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    // Obtener balance general
                    Mono<BalanceDTO> balanceMono = balanceClient.getCustomerBalance(customerId)
                            .onErrorResume(e -> Mono.just(new BalanceDTO()));

                    // Obtener transacciones recientes (últimas 10)
                    Mono<List<TransactionDTO>> transactionsMono = transactionClient.getRecentTransactions(customerId, 10)
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    // Combinar todos los resultados
                    return Mono.zip(
                            accountsMono,
                            creditsMono,
                            creditCardsMono,
                            balanceMono,
                            transactionsMono
                    ).map(tuple -> {
                        summary.setAccounts(tuple.getT1());
                        summary.setCredits(tuple.getT2());
                        summary.setCreditCards(tuple.getT3());
                        summary.setBalance(tuple.getT4());
                        summary.setRecentTransactions(tuple.getT5());
                        return summary;
                    });
                });
    }

    @Override
    public Mono<DebtStatusDTO> validateOverdueDebt(String customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .flatMap(customer -> {
                    // Verificar créditos vencidos con mejor manejo de errores
                    Mono<Boolean> creditOverdueMono = creditClient.hasOverdueCredits(customerId)
                            .onErrorReturn(false);

                    // Verificar tarjetas de crédito vencidas con mejor manejo de errores
                    Mono<Boolean> creditCardOverdueMono = creditCardClient.hasOverdueCreditCards(customerId)
                            .onErrorReturn(false);

                    // Obtener detalles de productos con deuda vencida con mejor manejo de errores
                    Mono<List<CreditDTO>> overdueCredits = creditClient.getCreditsByCustomerId(customerId)
                            .filter(credit -> credit.getIsOverdue() != null && credit.getIsOverdue())
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    Mono<List<CreditCardDTO>> overdueCreditCards = creditCardClient.getCreditCardsByCustomerId(customerId)
                            .filter(card -> card.getIsOverdue() != null && card.getIsOverdue())
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    return Mono.zip(creditOverdueMono, creditCardOverdueMono, overdueCredits, overdueCreditCards)
                            .map(tuple -> {
                                boolean hasOverdueCredit = tuple.getT1();
                                boolean hasOverdueCreditCard = tuple.getT2();
                                List<CreditDTO> overdueCreditslist = tuple.getT3();
                                List<CreditCardDTO> overdueCreditCardslist = tuple.getT4();

                                DebtStatusDTO debtStatus = new DebtStatusDTO();
                                debtStatus.setHasOverdueDebt(hasOverdueCredit || hasOverdueCreditCard);

                                // Calcular el monto total vencido y preparar la lista de productos
                                double totalOverdueAmount = 0.0;
                                List<OverdueProductDTO> overdueProducts = new ArrayList<>();

                                // Procesar créditos vencidos con validaciones adicionales
                                for (CreditDTO credit : overdueCreditslist) {
                                    if (credit.getIsOverdue() != null && credit.getIsOverdue() &&
                                            credit.getOutstandingDebt() != null) {
                                        totalOverdueAmount += credit.getOutstandingDebt();

                                        OverdueProductDTO product = new OverdueProductDTO();
                                        product.setProductId(credit.getCreditId());
                                        product.setProductType("CREDIT");
                                        product.setOverdueAmount(credit.getOutstandingDebt());
                                        product.setDueDate(""); // Establecer un valor por defecto si no tienes la fecha

                                        overdueProducts.add(product);
                                    }
                                }

                                // Procesar tarjetas de crédito vencidas con validaciones adicionales
                                for (CreditCardDTO card : overdueCreditCardslist) {
                                    if (card.getIsOverdue() != null && card.getIsOverdue() &&
                                            card.getCurrentBalance() != null) {
                                        totalOverdueAmount += card.getCurrentBalance();

                                        OverdueProductDTO product = new OverdueProductDTO();
                                        product.setProductId(card.getCardId());
                                        product.setProductType("CREDIT_CARD");
                                        product.setOverdueAmount(card.getCurrentBalance());
                                        product.setDueDate(""); // Establecer un valor por defecto si no tienes la fecha

                                        overdueProducts.add(product);
                                    }
                                }

                                debtStatus.setTotalOverdueAmount(totalOverdueAmount);
                                debtStatus.setOverdueProducts(overdueProducts);

                                return debtStatus;
                            })
                            .onErrorResume(e -> {
                                // Manejar cualquier otro error no previsto
                                DebtStatusDTO fallbackStatus = new DebtStatusDTO();
                                fallbackStatus.setHasOverdueDebt(false);
                                fallbackStatus.setTotalOverdueAmount(0.0);
                                fallbackStatus.setOverdueProducts(new ArrayList<>());
                                return Mono.just(fallbackStatus);
                            });
                });
    }

    @Override
    public Mono<ProductReportDTO> generateProductReport(String customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(customerId)))
                .flatMap(customer -> {
                    ProductReportDTO report = new ProductReportDTO();
                    report.setCustomerId(customer.getId());
                    report.setDocumentNumber(customer.getDocumentNumber());
                    report.setCustomerType(customer.getCustomerType());

                    // Establecer el nombre según el tipo de cliente
                    if (customer.getCustomerType() == CustomerType.PERSONAL
                            || customer.getCustomerType() == CustomerType.PERSONAL_VIP) {
                        report.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
                    } else {
                        report.setCustomerName(customer.getBusinessName());
                    }

                    // Obtener cuentas
                    Mono<List<AccountDTO>> accountsMono = accountClient.getAccountsByCustomerId(customerId)
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    // Obtener créditos
                    Mono<List<CreditDTO>> creditsMono = creditClient.getCreditsByCustomerId(customerId)
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    // Obtener tarjetas de crédito
                    Mono<List<CreditCardDTO>> creditCardsMono = creditCardClient.getCreditCardsByCustomerId(customerId)
                            .collectList()
                            .onErrorReturn(new ArrayList<>());

                    // Verificar deuda vencida
                    Mono<Boolean> hasOverdueMono = this.validateOverdueDebt(customerId)
                            .map(DebtStatusDTO::getHasOverdueDebt)
                            .onErrorReturn(false);

                    return Mono.zip(accountsMono, creditsMono, creditCardsMono, hasOverdueMono)
                            .map(tuple -> {
                                List<AccountDTO> accounts = tuple.getT1();
                                List<CreditDTO> credits = tuple.getT2();
                                List<CreditCardDTO> creditCards = tuple.getT3();
                                Boolean hasOverdueDebt = tuple.getT4();

                                // Calcular totales
                                report.setTotalAccounts(accounts.size());
                                report.setTotalCredits(credits.size());
                                report.setTotalCreditCards(creditCards.size());
                                report.setHasOverdueDebt(hasOverdueDebt);

                                // Calcular saldo total de depósitos
                                Double totalDeposits = accounts.stream()
                                        .mapToDouble(AccountDTO::getBalance)
                                        .sum();
                                report.setTotalDeposits(totalDeposits);

                                // Calcular monto total de préstamos
                                Double totalLoans = credits.stream()
                                        .mapToDouble(CreditDTO::getOutstandingDebt)
                                        .sum();
                                report.setTotalLoans(totalLoans);

                                // Agregar detalles de productos
                                report.setAccounts(accounts);
                                report.setCredits(credits);
                                report.setCreditCards(creditCards);

                                return report;
                            });
                });
    }
}


package com.example.mscustomerservice.service.impl;


import com.example.mscustomerservice.dto.BusinessCustomerDTO;
import com.example.mscustomerservice.dto.BusinessPymeCustomerDTO;
import com.example.mscustomerservice.dto.PersonalCustomerDTO;
import com.example.mscustomerservice.dto.PersonalVipCustomerDTO;
import com.example.mscustomerservice.exception.CustomerNotFoundException;
import com.example.mscustomerservice.model.Customer;
import com.example.mscustomerservice.model.CustomerType;
import com.example.mscustomerservice.repository.CustomerRepository;
import com.example.mscustomerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

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
}

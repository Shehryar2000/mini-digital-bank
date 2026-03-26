package com.mini.bank.customer.service;

import com.mini.bank.customer.entity.Customer;
import com.mini.bank.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(String name, String email) {

        customerRepository.findByEmail(email).ifPresent(customer -> {
            throw new RuntimeException("Customer already exists");
        });

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);

        return customerRepository.save(customer);
    }
}

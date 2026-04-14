package com.mini.bank.customer.service;

import com.mini.bank.auth.entity.User;
import com.mini.bank.customer.dto.CustomerResponse;
import com.mini.bank.customer.dto.UpdateCustomerRequest;
import com.mini.bank.customer.entity.Customer;
import com.mini.bank.customer.repository.CustomerRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomerInternal(User user, String name, String email) {

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setUser(user);
        return customerRepository.saveAndFlush(customer);
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    public CustomerResponse getCurrentCustomer() {

        Customer customer = getCustomerObject();

        CustomerResponse response = CustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .customerNumber(customer.getCustomerNumber())
                .build();

        return response;
    }

    private Customer getCustomerObject() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Claims claims = (Claims) auth.getDetails();
        UUID customerId = UUID.fromString(claims.get("customerId").toString());
        return customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void updateCurrentCustomer(UpdateCustomerRequest request) {

        Customer customer = getCustomerObject();

        //Update name if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            customer.setName(request.getName());
        }

        //Update email if provided
        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            boolean exists = customerRepository.existsByEmail(request.getEmail());

            if (exists && !request.getEmail().equals(customer.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            customer.setEmail(request.getEmail());
        }
        customerRepository.save(customer);
    }
}

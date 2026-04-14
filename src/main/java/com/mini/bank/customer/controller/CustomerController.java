package com.mini.bank.customer.controller;

import com.mini.bank.customer.dto.CustomerResponse;
import com.mini.bank.customer.dto.UpdateCustomerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mini.bank.customer.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/myself")
    public ResponseEntity<CustomerResponse> getCurrentCustomer() {
        CustomerResponse response = customerService.getCurrentCustomer();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/myself")
    public ResponseEntity<?> updateCurrentCustomer(@RequestBody UpdateCustomerRequest request) {
        customerService.updateCurrentCustomer(request);
        return ResponseEntity.status(HttpStatus.OK).body("Customer updated successfully");
    }

}
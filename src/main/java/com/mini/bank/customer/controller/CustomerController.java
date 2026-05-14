package com.mini.bank.customer.controller;

import com.mini.bank.common.util.IpUtil;
import com.mini.bank.corebanking.dto.ApiResponse;
import com.mini.bank.customer.dto.CustomerResponse;
import com.mini.bank.customer.dto.UpdateCustomerRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
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

    @Operation(
            summary = "Get Current Customer",
            description = "Returns current customer profile"
    )
    @GetMapping("/myself")
    public ResponseEntity<CustomerResponse> getCurrentCustomer(HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        CustomerResponse response = customerService.getCurrentCustomer(ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateCurrentCustomer(@RequestBody UpdateCustomerRequest request, HttpServletRequest http) {
        String ip = IpUtil.getClientIp(http);
        ApiResponse response = customerService.updateCurrentCustomer(request, ip);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
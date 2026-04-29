package com.mini.bank.customer.service;

import com.mini.bank.audit.enums.AuditAction;
import com.mini.bank.audit.enums.AuditEntityType;
import com.mini.bank.audit.service.AuditService;
import com.mini.bank.auth.entity.User;
import com.mini.bank.common.exception.EmailAlreadyExistsException;
import com.mini.bank.common.exception.NoChangesDetectedException;
import com.mini.bank.common.exception.UsernameAlreadyExistsException;
import com.mini.bank.common.security.AuthContext;
import com.mini.bank.customer.dto.CustomerResponse;
import com.mini.bank.customer.dto.UpdateCustomerResponse;
import com.mini.bank.customer.dto.UpdateCustomerRequest;
import com.mini.bank.customer.entity.Customer;
import com.mini.bank.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final AuditService auditService;
    private final CustomerRepository customerRepository;
    private final AuthContext authContext;

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

    public CustomerResponse getCurrentCustomer(String ip) {

        UUID userId = authContext.getUserId();
        UUID customerId = null;

        try {
            Customer customer = getCustomer(userId);
            customerId = customer.getId();

            // Audit Maintaining - Customer Fetch Success
            auditService.success(
                    userId,
                    AuditAction.CUSTOMER_FETCH,
                    ip,
                    AuditEntityType.CUSTOMER,
                    customerId,
                    null
            );

            return CustomerResponse.builder()
                    .customerId(customerId)
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .customerNumber(customer.getCustomerNumber())
                    .build();

        } catch (Exception e) {

            auditService.failure(
                    userId,
                    AuditAction.CUSTOMER_FETCH,
                    ip,
                    AuditEntityType.CUSTOMER,
                    customerId,
                    errorMeta(e)
            );
            throw e;
        }
    }

    public UpdateCustomerResponse updateCurrentCustomer(UpdateCustomerRequest request, String ip) {

        UUID userId = authContext.getUserId();
        UUID customerId = null;

        try {

            Customer customer = getCustomer(userId);
            customerId = customer.getId();

            String oldName = customer.getName();
            String oldEmail = customer.getEmail();

            boolean updated = false;

            //Update name if provided
            if (request.getName() != null && !request.getName().isBlank() && !request.getName().equals(oldName)) {
                customer.setName(request.getName());
                updated = true;
            } else if (request.getName() != null && !request.getName().isBlank() && request.getName().equals(oldName)) {

                auditService.failure(
                        userId,
                        AuditAction.CUSTOMER_UPDATED,
                        ip,
                        AuditEntityType.CUSTOMER,
                        customerId,
                        Map.of(
                                "oldName", oldName,
                                "newName", request.getName(),
                                "reason", "No change in username detected"
                        )
                );
                throw new NoChangesDetectedException("No change in username detected");

            }

            //Update email if provided
            if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().equals(oldEmail)) {

                System.out.println("Inside Email if");
                if (customerRepository.existsByEmail(request.getEmail())) {

                    System.out.println("Email already exists");
                    auditService.failure(
                            userId,
                            AuditAction.CUSTOMER_UPDATED,
                            ip,
                            AuditEntityType.CUSTOMER,
                            customerId,
                            Map.of(
                                    "email", request.getEmail(),
                                    "reason", "email already exists"
                            )
                    );
                    throw new EmailAlreadyExistsException("Email already exists");
                } else {
                    System.out.println("Inside Else");
                    customer.setEmail(request.getEmail());
                    updated = true;
                }
            } else if (request.getEmail() !=null && !request.getEmail().isBlank() && request.getEmail().equals(oldEmail)) {
                System.out.println("Inside Email else-if");
                updated = false;

                auditService.failure(
                        userId,
                        AuditAction.CUSTOMER_UPDATED,
                        ip,
                        AuditEntityType.CUSTOMER,
                        customerId,
                        Map.of(
                                "oldEmail", oldEmail,
                                "newEmail", request.getEmail(),
                                "reason", "No change in email detected"
                        )
                );
                throw new NoChangesDetectedException("No change in email detected");
            }

//            if (!updated) {
//
//                auditService.failure(
//                        userId,
//                        AuditAction.CUSTOMER_UPDATED,
//                        ip,
//                        AuditEntityType.CUSTOMER,
//                        customerId,
//                        Map.of(
//                                "reason", "no fields changed",
//                                "requestedName", request.getName(),
//                                "requestedEmail", request.getEmail()
//                        )
//                );
//
//                throw new NoChangesDetectedException("No fields changed");
//            }
            customerRepository.save(customer);

            Map<String, Object> metadata = new HashMap<>();

            if (!oldName.equals(customer.getName())) {
                metadata.put("oldName", oldName);
                metadata.put("newName", customer.getName());
            }

            if (!oldEmail.equals(customer.getEmail())) {
                metadata.put("oldEmail", oldEmail);
                metadata.put("newEmail", customer.getEmail());
            }

            // Audit Maintaining - Customer Update Success
            auditService.success(
                    userId,
                    AuditAction.CUSTOMER_UPDATED,
                    ip,
                    AuditEntityType.CUSTOMER,
                    customerId,
                    metadata
            );

            return UpdateCustomerResponse.builder()
                    .message("Customer updated successfully")
                    .build();
        } catch (EmailAlreadyExistsException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {

            // Audit Maintaining - Customer Update Failure
            auditService.failure(
                    userId,
                    AuditAction.CUSTOMER_UPDATED,
                    ip,
                    AuditEntityType.CUSTOMER,
                    customerId,
                    errorMeta(e)
            );

            throw e;
        }
    }

    private Customer getCustomer(UUID userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    }

    private Map<String, Object> errorMeta(Exception e) {
        return Map.of(
                "error", e.getClass().getSimpleName(),
                "message", e.getMessage()
        );
    }
}

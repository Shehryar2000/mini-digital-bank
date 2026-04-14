package com.mini.bank.customer.repository;

import com.mini.bank.auth.entity.User;
import com.mini.bank.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmail(String email);

}

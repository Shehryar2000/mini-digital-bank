package com.mini.bank.account.repository;

import com.mini.bank.account.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(Long accountNumber);

    @Query(value = "SELECT nextval('account_seq')", nativeQuery = true)
    Long getNextAccountNumber();

    List<Account> findByCustomerId(UUID customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT a from Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(UUID id);

}

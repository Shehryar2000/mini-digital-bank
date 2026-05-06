package com.mini.bank.transfer.repository;

import com.mini.bank.transfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    @Query(value = "SELECT nextval('txn_seq')", nativeQuery = true)
    Long getNextRefNumber();
}

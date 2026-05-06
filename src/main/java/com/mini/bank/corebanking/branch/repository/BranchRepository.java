package com.mini.bank.corebanking.branch.repository;

import com.mini.bank.corebanking.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Integer> {
}

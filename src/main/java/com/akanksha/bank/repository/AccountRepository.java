package com.akanksha.bank.repository;

import com.akanksha.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Get accounts of logged-in user
    List<Account> findByUserEmail(String email);

    // Use account number instead of ID
    Optional<Account> findByAccountNumber(String accountNumber);
}
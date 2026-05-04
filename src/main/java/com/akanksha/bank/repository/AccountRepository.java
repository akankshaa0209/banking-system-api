package com.akanksha.bank.repository;

import com.akanksha.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Get accounts of logged-in user
    List<Account> findByUserEmail(String email);
}
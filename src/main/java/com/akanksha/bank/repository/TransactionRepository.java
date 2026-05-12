package com.akanksha.bank.repository;

import com.akanksha.bank.entity.Transaction;
import com.akanksha.bank.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

//for pagination, we can return Page<Transaction> instead of List<Transaction> and accept Pageable as a parameter
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

        List<Transaction> findByAccountId(Long accountId);

        List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);

        List<Transaction> findByAccountIdAndTimestampBetweenOrderByTimestampDesc(
                        Long accountId,
                        LocalDateTime start,
                        LocalDateTime end);

        List<Transaction> findByAccountIdAndTypeOrderByTimestampDesc(
                        Long accountId,
                        TransactionType type);

        List<Transaction> findByAccountIdAndTypeAndTimestampBetweenOrderByTimestampDesc(
                        Long accountId,
                        TransactionType type,
                        LocalDateTime start,
                        LocalDateTime end);

        // For pagination, we can return Page<Transaction> instead of List<Transaction>
        // and accept Pageable as a parameter
        Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
}
package com.akanksha.bank.repository;

import com.akanksha.bank.entity.Transaction;
import com.akanksha.bank.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

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
}
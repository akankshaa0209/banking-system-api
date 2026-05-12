package com.akanksha.bank.service;

import com.akanksha.bank.entity.*;
import com.akanksha.bank.repository.AccountRepository;
import com.akanksha.bank.repository.UserRepository;
import com.akanksha.bank.repository.TransactionRepository;
import com.akanksha.bank.exception.ResourceNotFoundException;
import com.akanksha.bank.exception.BadRequestException;
import com.akanksha.bank.dto.TransferRequest;
import com.akanksha.bank.dto.DepositRequest;
import com.akanksha.bank.dto.WithdrawRequest;
import com.akanksha.bank.dto.TransactionResponse;
import com.akanksha.bank.dto.StatementResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// For pagination, we can return Page<TransactionResponse> instead of List<TransactionResponse>
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Get logged-in user
    private String getLoggedInUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    // CREATE ACCOUNT
    public Account createAccount() {

        String email = getLoggedInUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = Account.builder()
                .accountNumber(UUID.randomUUID().toString())
                .balance(0.0)
                .user(user)
                .build();

        return accountRepository.save(account);
    }

    // GET MY ACCOUNTS
    public List<Account> getMyAccounts() {
        return accountRepository.findByUserEmail(getLoggedInUserEmail());
    }

    // GET ACCOUNT BY ID
    public Account getAccountById(Long id) {

        String email = getLoggedInUserEmail();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Access denied");
        }

        return account;
    }

    // DELETE ACCOUNT
    public void deleteAccount(Long id) {
        Account account = getAccountById(id);
        accountRepository.delete(account);
    }

    // TRANSFER
    public String transfer(TransferRequest request) {

        String email = getLoggedInUserEmail();

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        // if (request.getFromAccountId().equals(request.getToAccountId())) {
        // throw new BadRequestException("Cannot transfer to same account");
        // }

        // Use account numbers instead of IDs for better security and usability
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new BadRequestException("Cannot transfer to same account");
        }

        Account fromAccount = accountRepository
                // .findById(request.getFromAccountId())
                // use account number instead of ID for better security and usability
                .findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("From account not found"));

        Account toAccount = accountRepository
                // .findById(request.getToAccountId())
                // use account number instead of ID for better security and usability
                .findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("To account not found"));

        if (!fromAccount.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Access denied");
        }

        if (fromAccount.getBalance() < request.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
        toAccount.setBalance(toAccount.getBalance() + request.getAmount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Save transactions (DEBIT + CREDIT)
        Transaction debitTxn = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .timestamp(LocalDateTime.now())
                .fromAccountId(fromAccount.getId())
                .toAccountId(toAccount.getId())
                .account(fromAccount)
                .build();

        Transaction creditTxn = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .timestamp(LocalDateTime.now())
                .fromAccountId(fromAccount.getId())
                .toAccountId(toAccount.getId())
                .account(toAccount)
                .build();

        transactionRepository.save(debitTxn);
        transactionRepository.save(creditTxn);

        return "Transfer successful";
    }

    // DEPOSIT
    public String deposit(DepositRequest request) {

        String email = getLoggedInUserEmail();

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Access denied");
        }

        account.setBalance(account.getBalance() + request.getAmount());
        accountRepository.save(account);

        // Transaction entry
        Transaction txn = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .timestamp(LocalDateTime.now())
                .account(account)
                .build();

        transactionRepository.save(txn);

        return "Amount deposited successfully";
    }

    // WITHDRAW
    public String withdraw(WithdrawRequest request) {

        String email = getLoggedInUserEmail();

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Access denied");
        }

        if (account.getBalance() < request.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - request.getAmount());
        accountRepository.save(account);

        // Transaction entry
        Transaction txn = Transaction.builder()
                .amount(request.getAmount())
                .type(TransactionType.WITHDRAW)
                .timestamp(LocalDateTime.now())
                .account(account)
                .build();

        transactionRepository.save(txn);

        return "Amount withdrawn successfully";
    }

    // transaction logging
    public List<TransactionResponse> getTransactions(Long accountId) {

        String email = getLoggedInUserEmail();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Access denied: Not your account");
        }

        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(tx -> TransactionResponse.builder()
                        .id(tx.getId())
                        .type(tx.getType().name())
                        .amount(tx.getAmount())
                        .fromAccount(tx.getFromAccountId() != null ? tx.getFromAccountId().toString() : null)
                        .toAccount(tx.getToAccountId() != null ? tx.getToAccountId().toString() : null)
                        .timestamp(tx.getTimestamp())
                        .build())
                .toList();
    }

    // for bank statement API with filters
    public List<StatementResponse> getStatement(
            Long accountId,
            LocalDateTime from,
            LocalDateTime to,
            String type) {

        String email = getLoggedInUserEmail();

        // 1. Validate ownership
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Access denied");
        }

        // 2. Fetch transactions based on filters
        List<Transaction> transactions;

        if (type != null && from != null && to != null) {
            transactions = transactionRepository
                    .findByAccountIdAndTypeAndTimestampBetweenOrderByTimestampDesc(
                            accountId,
                            TransactionType.valueOf(type),
                            from,
                            to);
        } else if (type != null) {
            transactions = transactionRepository
                    .findByAccountIdAndTypeOrderByTimestampDesc(
                            accountId,
                            TransactionType.valueOf(type));
        } else if (from != null && to != null) {
            transactions = transactionRepository
                    .findByAccountIdAndTimestampBetweenOrderByTimestampDesc(
                            accountId,
                            from,
                            to);
        } else {
            transactions = transactionRepository
                    .findByAccountIdOrderByTimestampDesc(accountId);
        }

        // 3. Map to response
        return transactions.stream()
                .map(tx -> StatementResponse.builder()
                        .id(tx.getId())
                        .type(tx.getType().name())
                        .amount(tx.getAmount())
                        .fromAccount(tx.getFromAccountId() != null ? tx.getFromAccountId().toString() : null)
                        .toAccount(tx.getToAccountId() != null ? tx.getToAccountId().toString() : null)
                        .timestamp(tx.getTimestamp())
                        .build())
                .toList();
    }

    // pagination method
    public Page<TransactionResponse> getTransactionsPaginated(
            Long accountId,
            int page,
            int size) {

        String email = getLoggedInUserEmail();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Access denied");
        }

        Pageable pageable = PageRequest.of(page, size);

        return transactionRepository
                .findByAccountId(accountId, pageable)
                .map(tx -> TransactionResponse.builder()
                        .id(tx.getId())
                        .type(tx.getType().name())
                        .amount(tx.getAmount())
                        .timestamp(tx.getTimestamp())
                        .build());
    }
}
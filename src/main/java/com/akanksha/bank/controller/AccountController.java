package com.akanksha.bank.controller;

import com.akanksha.bank.entity.Account;
import com.akanksha.bank.service.AccountService;
import com.akanksha.bank.dto.TransferRequest;
import com.akanksha.bank.dto.TransactionResponse;
import com.akanksha.bank.dto.StatementResponse;

import com.akanksha.bank.dto.DepositRequest;
import com.akanksha.bank.dto.WithdrawRequest;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Create account
    @PostMapping
    public Account createAccount() {
        return accountService.createAccount();
    }

    // Get my accounts
    @GetMapping
    public List<Account> getMyAccounts() {
        return accountService.getMyAccounts();
    }

    // Get account by ID
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    // Delete account
    @DeleteMapping("/{id}")
    public String deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return "Account deleted successfully";
    }

    // Transfer money
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        return accountService.transfer(request);
    }

    @PostMapping("/deposit")
    public String deposit(@RequestBody DepositRequest request,
            Authentication authentication) {

        String email = authentication.getName(); // from JWT

        return accountService.deposit(request);
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestBody WithdrawRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return accountService.withdraw(request);
    }

    // Get transactions logging
    @GetMapping("/{id}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        return accountService.getTransactions(id);
    }

    // for bank statement API with filters (date range, type)
    @GetMapping("/{id}/statement")
    public List<StatementResponse> getStatement(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String type) {

        LocalDateTime fromDate = (from != null) ? LocalDateTime.parse(from) : null;
        LocalDateTime toDate = (to != null) ? LocalDateTime.parse(to) : null;

        return accountService.getStatement(id, fromDate, toDate, type);
    }
}
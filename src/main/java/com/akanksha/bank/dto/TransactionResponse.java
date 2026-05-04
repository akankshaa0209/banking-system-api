package com.akanksha.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private String type; // DEPOSIT / WITHDRAW / TRANSFER
    private Double amount;

    private String fromAccount;
    private String toAccount;

    private LocalDateTime timestamp;
}
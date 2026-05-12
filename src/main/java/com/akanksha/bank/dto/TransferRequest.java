package com.akanksha.bank.dto;

import lombok.Data;

@Data
public class TransferRequest {

    // private Long fromAccountId;
    // private Long toAccountId;

    // Use account numbers instead of IDs for better security and usability
    private String fromAccountNumber;
    private String toAccountNumber;

    private Double amount;
}
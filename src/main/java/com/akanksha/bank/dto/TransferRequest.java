package com.akanksha.bank.dto;

import lombok.Data;

@Data
public class TransferRequest {

    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
}
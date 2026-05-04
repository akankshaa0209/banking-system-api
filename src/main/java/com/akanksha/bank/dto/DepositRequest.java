package com.akanksha.bank.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private Long accountId;
    private Double amount;
}
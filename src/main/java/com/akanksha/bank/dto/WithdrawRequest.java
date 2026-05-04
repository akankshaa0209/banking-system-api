package com.akanksha.bank.dto;

import lombok.Data;

@Data
public class WithdrawRequest {
    private Long accountId;
    private Double amount;
}
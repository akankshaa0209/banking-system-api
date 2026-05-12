package com.akanksha.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StatementResponse {

    private Long id;
    private String type;
    private Double amount;
    private String fromAccount;
    private String toAccount;
    private LocalDateTime timestamp;
}
package com.nitex.simplebankapp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AccountStatementResponse {
    private LocalDateTime transactionDate; // Deposit or Withdrawal
    private String transactionType;
    private String narration;
    private Double amount;
    private  Double accountBalance;




}

package com.nitex.simplebankapp.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest {
    private String accountNumber;
    private String accountPassword;
    private Double withdrawnAmount;
}

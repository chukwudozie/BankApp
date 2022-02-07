package com.nitex.simplebankapp.model;

import com.nitex.simplebankapp.payload.response.AccountStatementResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Account {

    private String accountNumber;
    private String accountName;
    private String role;
    private String password;
    private Double balance;
    private List<AccountStatementResponse> statement;

    public Account() {
        statement = new CopyOnWriteArrayList<>();
    }

    public Account(String accountNumber, String accountName, String role, String password,
                   Double balance) {
        this();
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.role = role;
        this.password = password;
        this.balance = balance;
    }
}

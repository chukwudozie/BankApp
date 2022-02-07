package com.nitex.simplebankapp.controller;


import com.nitex.simplebankapp.exception.AccountResponseException;
import com.nitex.simplebankapp.payload.request.AccountLoginRequest;
import com.nitex.simplebankapp.payload.request.CreateAccountRequest;
import com.nitex.simplebankapp.payload.request.DepositRequest;
import com.nitex.simplebankapp.payload.request.WithdrawalRequest;
import com.nitex.simplebankapp.payload.response.AccountInfoResponse;
import com.nitex.simplebankapp.payload.response.AccountLoginResponse;
import com.nitex.simplebankapp.payload.response.AccountStatementResponse;
import com.nitex.simplebankapp.payload.response.TransactionResponse;
import com.nitex.simplebankapp.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create_account")
    public ResponseEntity<TransactionResponse> createAccount(@RequestBody CreateAccountRequest request) {
        TransactionResponse response = accountService.createAccount(request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResponseCode()));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> depositTransaction(@RequestBody DepositRequest request) {
        TransactionResponse response = accountService.deposit(request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResponseCode()));
    }

    @GetMapping("/account_info/{accountNumber}")
    public ResponseEntity<AccountInfoResponse> getAccountInfo(@PathVariable("accountNumber") String accountNumber) {
        AccountInfoResponse response = accountService.getAccountInfo(accountNumber);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResponseCode()));
    }

    @GetMapping("/account_statement/{accountNumber}")
    public ResponseEntity<List<AccountStatementResponse>> getAccountStatement(@PathVariable("accountNumber") String accountNumber) {
        List<AccountStatementResponse> response = accountService.viewAccountStatement(accountNumber);
        return new ResponseEntity<>(response, HttpStatus.valueOf(200));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody AccountLoginRequest loginRequest) throws Exception {
        AccountLoginResponse response = accountService.loginAccount(loginRequest);
        if(!response.isSuccess()) {
            throw new AccountResponseException(HttpStatus.UNAUTHORIZED,"Unauthorized");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionResponse> withdrawalTransaction(@RequestBody WithdrawalRequest request) throws Exception {
        TransactionResponse response = accountService.withdraw(request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getResponseCode()));
    }
}

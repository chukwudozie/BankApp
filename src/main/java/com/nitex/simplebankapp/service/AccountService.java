package com.nitex.simplebankapp.service;

import com.nitex.simplebankapp.payload.request.AccountLoginRequest;
import com.nitex.simplebankapp.payload.request.CreateAccountRequest;
import com.nitex.simplebankapp.payload.request.DepositRequest;
import com.nitex.simplebankapp.payload.request.WithdrawalRequest;
import com.nitex.simplebankapp.payload.response.AccountInfoResponse;
import com.nitex.simplebankapp.payload.response.AccountLoginResponse;
import com.nitex.simplebankapp.payload.response.AccountStatementResponse;
import com.nitex.simplebankapp.payload.response.TransactionResponse;

import java.util.List;

public interface AccountService {

    TransactionResponse createAccount(CreateAccountRequest accountRequest);
    AccountInfoResponse getAccountInfo(String accountNumber);
    List<AccountStatementResponse> viewAccountStatement(String accountNumber);
    TransactionResponse deposit(DepositRequest depositRequest);
    TransactionResponse withdraw(WithdrawalRequest request) throws Exception;
    AccountLoginResponse loginAccount(AccountLoginRequest loginRequest) throws Exception;

}

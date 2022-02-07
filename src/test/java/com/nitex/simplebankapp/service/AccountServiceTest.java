package com.nitex.simplebankapp.service;

import com.nitex.simplebankapp.model.Account;
import com.nitex.simplebankapp.payload.request.CreateAccountRequest;
import com.nitex.simplebankapp.payload.response.AccountInfoResponse;
import com.nitex.simplebankapp.payload.response.AccountResponse;
import com.nitex.simplebankapp.payload.response.TransactionResponse;
import com.nitex.simplebankapp.repo.AccountRepository;
import com.nitex.simplebankapp.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    AuthenticationManager authenticationManager;

    UserDetailsService userDetailsService;

    JwtUtils utils;

    AccountRepository accountRepository = new AccountRepository();

    @InjectMocks
    AccountService accountService = new AccountService(encoder,authenticationManager,
            userDetailsService,utils,accountRepository);
    Account account;
    @BeforeEach
    void setUp() {
        account = new Account();
        account.setAccountName("Emeka");
        account.setPassword("123");
        account.setBalance(500.0);

    }

    @Test
    void createAccount() {
        CreateAccountRequest request = new CreateAccountRequest(account.getAccountName(),account.getPassword(),400.0);

        TransactionResponse expectedResponse = accountService.createAccount(request);
        TransactionResponse  actualResponse =  new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Initial Deposit cannot be less than #500");
       final String expected = expectedResponse.getMessage();
       final String actual = actualResponse.getMessage();
       final boolean expectedStatus = expectedResponse.isSuccess();
       final  boolean actualStatus = actualResponse.isSuccess();
        assertEquals(expected,actual);
        assertEquals(expectedStatus,actualStatus);
        CreateAccountRequest request1 = new CreateAccountRequest("Emeka","123",600.00);
        TransactionResponse expectedResponse1 = accountService.createAccount(request1);
        assertTrue(expectedResponse1.isSuccess());
        Optional <Account> account = accountRepository.findByAccountName(request.getAccountName());
        assertNotNull(account.get().getAccountNumber());
        assertEquals(account.get().getAccountName(), request.getAccountName());

        System.out.println(account.get().getAccountNumber());





    }

    @Test
    void getAccountInfo() {
        CreateAccountRequest request = new CreateAccountRequest(account.getAccountName(),account.getPassword(),account.getBalance());
        var t =accountService.createAccount(request);
        var e = accountRepository.findByAccountName(account.getAccountName());
       var expected = accountService.getAccountInfo(request.getAccountName());
        var result = new AccountInfoResponse(HttpStatus.BAD_REQUEST.value(),
                false, "Account Number Not found",  new AccountResponse("", "", 0.0));

        assertEquals(expected.getMessage(),result.getMessage());
        assertEquals(expected.getResponseCode(),result.getResponseCode());
        assertEquals(expected.getAccountResponse().getBalance(),result.getAccountResponse().getBalance());
        assertNotNull(e.get().getAccountNumber());


//        System.out.println(account.getAccountName());
//        System.out.println(account.getPassword());
//        System.out.println(account.getBalance());
//        System.out.println(account.getAccountNumber());
//        System.out.println(t.getMessage());
//
//        System.out.println(e.get().getAccountNumber());
    }

    @Test
    void viewAccountStatement(){
        CreateAccountRequest request = new CreateAccountRequest(account.getAccountName(),
                account.getPassword(),account.getBalance());
        TransactionResponse expectedResponse = accountService.createAccount(request);
        Optional<Account> account = accountRepository.findByAccountName(request.getAccountName());
        String acctNumber = account.get().getAccountNumber();
        System.out.println(account.get().getAccountNumber());
        var output = accountService.viewAccountStatement(acctNumber);
        assertEquals(output,account.get().getStatement());
        assertTrue(expectedResponse.getMessage().contains(acctNumber));
        System.out.println(output);
        System.out.println(account.get().getStatement());
        System.out.println(expectedResponse.getMessage());


    }

    @Test
    void deposit(){

    }


}
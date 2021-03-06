package com.nitex.simplebankapp.service;

import com.nitex.simplebankapp.model.Account;
import com.nitex.simplebankapp.payload.request.CreateAccountRequest;
import com.nitex.simplebankapp.payload.request.DepositRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    UserDetailsService userDetailsService;
    Account account;


    JwtUtils utils = new JwtUtils("chompfoodSecretKeychangedbyme",86400000);

    @Mock
    AuthenticationManager authenticationManager  = authentication -> authentication;


    AccountRepository accountRepository = new AccountRepository();


    @InjectMocks
    AccountServiceImpl accountServiceImpl = new AccountServiceImpl(encoder,authenticationManager,
            userDetailsService,utils,accountRepository);

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

        TransactionResponse expectedResponse = accountServiceImpl.createAccount(request);
        TransactionResponse  actualResponse =  new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Initial Deposit cannot be less than #500");
       final String expected = expectedResponse.getMessage();
       final String actual = actualResponse.getMessage();
       final boolean expectedStatus = expectedResponse.isSuccess();
       final  boolean actualStatus = actualResponse.isSuccess();
        assertEquals(expected,actual);
        assertEquals(expectedStatus,actualStatus);
        CreateAccountRequest request1 = new CreateAccountRequest("Emeka","123",600.00);
        TransactionResponse expectedResponse1 = accountServiceImpl.createAccount(request1);
        assertNotNull(expectedResponse1);
        Optional <Account> account = accountRepository.findByAccountName(request.getAccountName());
        assertNotNull(account.get().getAccountNumber());
        assertEquals(account.get().getAccountName(), request.getAccountName());

        System.out.println(account.get().getAccountNumber());





    }

    @Test
    void getAccountInfo() {
        CreateAccountRequest request = new CreateAccountRequest(account.getAccountName(),account.getPassword(),account.getBalance());
        var t = accountServiceImpl.createAccount(request);
        var e = accountRepository.findByAccountName(account.getAccountName());
       var expected = accountServiceImpl.getAccountInfo(request.getAccountName());
        var result = new AccountInfoResponse(HttpStatus.BAD_REQUEST.value(),
                false, "Account Number Not found",  new AccountResponse("", "", 0.0));

        assertEquals(expected.getMessage(),result.getMessage());
        assertEquals(expected.getResponseCode(),result.getResponseCode());
        assertEquals(expected.getAccountResponse().getBalance(),result.getAccountResponse().getBalance());
        assertNotNull(e.get().getAccountNumber());

    }

    @Test
    void viewAccountStatement(){
        CreateAccountRequest request = new CreateAccountRequest(account.getAccountName(),
                account.getPassword(),account.getBalance());
        TransactionResponse expectedResponse = accountServiceImpl.createAccount(request);
        Optional<Account> account = accountRepository.findByAccountName(request.getAccountName());
        String acctNumber = account.get().getAccountNumber();
        System.out.println(account.get().getAccountNumber());
        var output = accountServiceImpl.viewAccountStatement(acctNumber);
        assertEquals(output,account.get().getStatement());

    }

    @Test
    void deposit(){

        CreateAccountRequest request = new CreateAccountRequest(account.getAccountName(),
                account.getPassword(),account.getBalance());
       accountServiceImpl.createAccount(request);
        Optional<Account> account1 = accountRepository.findByAccountName(request.getAccountName());
        String acctNumber = account1.get().getAccountNumber();
        Double depositAmount = 700.00;
        DepositRequest depositRequest = new DepositRequest(acctNumber,depositAmount);
        accountServiceImpl.deposit(depositRequest);
        assertEquals(account1.get().getBalance(),depositAmount + account.getBalance());

    }
}
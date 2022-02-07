package com.nitex.simplebankapp.service;

import com.nitex.simplebankapp.exception.AccountResponseException;
import com.nitex.simplebankapp.exception.WrongCredentialsException;
import com.nitex.simplebankapp.model.Account;
import com.nitex.simplebankapp.payload.request.AccountLoginRequest;
import com.nitex.simplebankapp.payload.request.CreateAccountRequest;
import com.nitex.simplebankapp.payload.request.DepositRequest;
import com.nitex.simplebankapp.payload.request.WithdrawalRequest;
import com.nitex.simplebankapp.payload.response.*;
import com.nitex.simplebankapp.repo.AccountRepository;
import com.nitex.simplebankapp.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountService {


    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private JwtUtils utils;
    private static final double MIN_DEPOSIT = 1;
    private static final double MAX_DEPOSIT = 1000000;
    private static final AccountResponse DEFAULT_ACCOUNT_RESPONSE = new AccountResponse("", "", 0.0);
    private static final String USER = "USER";
    private final AccountRepository accountRepository;

    private static int inc = 0;

    private static String accountNumberGenerator(){
        Random rand = new Random();
        var randomInt = String.valueOf(rand.nextInt(88) + 11);

        long id = Long.parseLong(randomInt + String.valueOf(System.currentTimeMillis())
                .substring(1,8)
                .concat(String.valueOf(inc)));
        inc = (inc+1)%10;
        return String.valueOf(id);
    }

    @Autowired
    public AccountService(PasswordEncoder encoder, AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService, JwtUtils utils, AccountRepository accountRepository) {
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.utils = utils;
        this.accountRepository = accountRepository;
    }


    public TransactionResponse createAccount(CreateAccountRequest accountRequest) {
        if (accountRequest.getInitialDeposit() < 500) {
            return new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Initial Deposit cannot be less than #500");
        }
        Optional<Account>  optionalAccount = accountRepository.findByAccountName(accountRequest.getAccountName());

        if (optionalAccount.isPresent()) {
            return new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Account Name Already Exist");
        }

        String accountNumber = accountNumberGenerator();
        Account account = new Account();
        account.setAccountName(accountRequest.getAccountName());
        account.setAccountNumber(accountNumber);
        account.setBalance(accountRequest.getInitialDeposit());
        account.setPassword(encoder.encode(accountRequest.getAccountPassword()));
        account.setRole(USER);
        var t = accountRepository.save(account);

        if (t != null) {
            TransactionResponse response = new TransactionResponse();
            response.setResponseCode(HttpStatus.OK.value());
            response.setSuccess(true);
            response.setMessage("Account Creation Successful, your new Account Number is: "+account.getAccountNumber());
            return response;
        } else {
            return new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Account was not created");
        }
    }


    public AccountInfoResponse getAccountInfo(String accountNumber) {
        var t = accountRepository.findByAccountNumber(accountNumber);
        if (t == null ) {
            return new AccountInfoResponse(HttpStatus.BAD_REQUEST.value(),
                    false, "Account Number Not found", DEFAULT_ACCOUNT_RESPONSE);
        } else {
            AccountInfoResponse response = new AccountInfoResponse();
            response.setResponseCode(HttpStatus.OK.value());
            response.setSuccess(true);
            response.setMessage("Account found");
            response.setAccountResponse(new AccountResponse(t.getAccountNumber(), t.getAccountName(), t.getBalance()));

            return response;
        }
    }

    public List<AccountStatementResponse> viewAccountStatement(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new AccountResponseException("Account with account number " + accountNumber + " does not exist");
        } else {
            return account.getStatement();
        }
    }

    public TransactionResponse deposit(DepositRequest depositRequest) {
        if (depositRequest.getAmount() < MIN_DEPOSIT && depositRequest.getAmount() > MAX_DEPOSIT) {
            return new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Deposit amount should be between #1 and #1000000");
        }
        Account account = accountRepository.findByAccountNumber(depositRequest.getAccountNumber());
        if (account == null) {
            return new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Account Does not exist");
        } else {
            account.setBalance(account.getBalance() + depositRequest.getAmount());
            AccountStatementResponse statement = new AccountStatementResponse(LocalDateTime.now(), "DEPOSIT",
                    "Account CREDITED with " + depositRequest.getAmount(), depositRequest.getAmount(), account.getBalance());
            account.getStatement().add(statement);

            return new TransactionResponse(HttpStatus.OK.value(), true, "Alert: ACCOUNT CREDITED WITH " + depositRequest.getAmount());
        }
    }

    public TransactionResponse withdraw(WithdrawalRequest request) throws Exception {
        AccountLoginRequest login = new AccountLoginRequest(request.getAccountNumber(), request.getAccountPassword());
        var loggedStatus = loginAccount(login);
        if (loggedStatus.isSuccess()) {
            Account account = accountRepository.findByAccountNumber(login.getAccountNumber());
            if (account.getBalance() - request.getWithdrawnAmount() >= 500) {
                account.setBalance(account.getBalance() - request.getWithdrawnAmount());
                AccountStatementResponse statement = new AccountStatementResponse(LocalDateTime.now(), "WITHDRAWAL",
                        "Account DEBITED with " + request.getWithdrawnAmount(), request.getWithdrawnAmount(), account.getBalance());
                account.getStatement().add(statement);
                return new TransactionResponse(HttpStatus.OK.value(), true, "Alert: ACCOUNT Debited with " + request.getWithdrawnAmount());
            }  else {
                return new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "Minimum balance after Withdrawal Should not be less than #500");
            }
        } else {
            return new TransactionResponse(HttpStatus.BAD_REQUEST.value(), false, "User not Authenticated");
        }
    }

    public AccountLoginResponse loginAccount(AccountLoginRequest loginRequest) throws Exception {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getAccountNumber(), loginRequest.getAccountPassword()));

            Account account = accountRepository.findByAccountNumber(loginRequest.getAccountNumber());

            String jwt = utils.generateJwtToken(authentication);

            if (!authentication.isAuthenticated() || jwt == null) {
                return new AccountLoginResponse(false, "No access token generated");
            }

        return new AccountLoginResponse(true,jwt);
        } catch (BadCredentialsException e) {
            throw new WrongCredentialsException("You entered an Invalid password!");
        }

    }


}

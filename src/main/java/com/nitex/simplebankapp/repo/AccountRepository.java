package com.nitex.simplebankapp.repo;


import com.nitex.simplebankapp.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountRepository {
    public static final Map<String, Account> accountDB = new ConcurrentHashMap<>();

    public Account findByAccountNumber(String accountNumber) {
        Account account = accountDB.get(accountNumber);

        return account;
    }

    public Account save(Account account) {
        accountDB.put(account.getAccountNumber(), account);
        return accountDB.get(account.getAccountNumber());
    }

    public Optional<Account> findByAccountName(String accountName) {
        Collection<Account> accountList =  accountDB.values();
        return accountList.stream()
                          .filter(account -> account.getAccountName().equals(accountName))
                          .findFirst();
    }

}

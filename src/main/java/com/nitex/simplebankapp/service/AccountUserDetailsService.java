package com.nitex.simplebankapp.service;

import com.nitex.simplebankapp.exception.WrongCredentialsException;
import com.nitex.simplebankapp.repo.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        var account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new WrongCredentialsException("Account Number Invalid");
        }
        return new AccountUserDetails(account);
    }
}

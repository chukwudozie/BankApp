package com.nitex.simplebankapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;

public class AccountResponseException extends ResponseStatusException {


    public AccountResponseException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public AccountResponseException(String reason) {
        this(HttpStatus.BAD_REQUEST, reason);
    }

}



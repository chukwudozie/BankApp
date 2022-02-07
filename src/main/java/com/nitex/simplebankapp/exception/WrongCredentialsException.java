package com.nitex.simplebankapp.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class WrongCredentialsException extends BadCredentialsException {

    public WrongCredentialsException(String msg) {
        super(msg);
    }
}

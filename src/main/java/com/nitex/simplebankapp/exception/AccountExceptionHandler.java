package com.nitex.simplebankapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class AccountExceptionHandler {
    @ExceptionHandler(value = AccountResponseException.class)
    public ResponseEntity<ExceptionResponse> apiRequestHandler(AccountResponseException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionResponse exception =  new ExceptionResponse(e.getReason(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exception, badRequest);
    }


    @ExceptionHandler(value = WrongCredentialsException.class)
    public ResponseEntity<ExceptionResponse> apiRequestHandler(WrongCredentialsException e){
        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        ExceptionResponse exception =  new ExceptionResponse(e.getMessage(),
                unauthorized,
                ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exception, unauthorized);
    }

}


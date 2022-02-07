package com.nitex.simplebankapp.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.*;

@Data
public class ExceptionResponse {

    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestamp;
//    private final String path;
    public ExceptionResponse(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {
        this.message = message;
        this.httpStatus = httpStatus;
//        this.path = path;
        this.timestamp = timestamp;
    }
}

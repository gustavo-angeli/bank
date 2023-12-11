package com.gusta.bank.exceptions.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gusta.bank.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ExceptionResponse exResponse;

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception ex, WebRequest request) {
        exResponse = new ExceptionResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), new Date().toString());
        return new ResponseEntity<>(exResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            NullPointerException.class,
            IllegalArgumentException.class
    })
    public final ResponseEntity<ExceptionResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
        exResponse = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), new Date().toString());
        return new ResponseEntity<>(exResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public final ResponseEntity<ExceptionResponse> handleUnauthorizedExceptions(Exception ex, WebRequest request) {
        exResponse = new ExceptionResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), new Date().toString());
        return new ResponseEntity<>(exResponse, HttpStatus.UNAUTHORIZED);
    }

}

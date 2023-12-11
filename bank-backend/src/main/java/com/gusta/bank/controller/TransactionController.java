package com.gusta.bank.controller;

import com.gusta.bank.domain.dto.TransactionDTO;
import com.gusta.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService service;

    @PatchMapping(value = "/deposit")
    public ResponseEntity<HttpStatus> deposit(@RequestBody TransactionDTO transactionDTO) {
        service.deposit(transactionDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(value = "/transfer")
    public ResponseEntity<HttpStatus> transfer(@RequestHeader(name = "Authorization") String token, @RequestBody TransactionDTO transactionDTO) {
        service.transfer(token, transactionDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

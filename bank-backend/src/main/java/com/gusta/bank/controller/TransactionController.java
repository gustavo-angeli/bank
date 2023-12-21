package com.gusta.bank.controller;

import com.gusta.bank.domain.dto.TransactionDTO;
import com.gusta.bank.service.TransactionService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transaction", description = "Transaction endpoints")
@RestController
@RequestMapping(value = "/api/v1/transaction")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService service;

    @ApiResponse(
            responseCode = "204",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )
    )
    @PatchMapping(value = "/deposit")
    public ResponseEntity<HttpStatus> deposit(@RequestBody TransactionDTO transactionDTO) {
        service.deposit(transactionDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponse(
            responseCode = "204",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )
    )
    @PatchMapping(value = "/transfer")
    public ResponseEntity<HttpStatus> transfer(@RequestHeader(name = "Authorization") String token, @RequestBody TransactionDTO transactionDTO) {
        service.transfer(token, transactionDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

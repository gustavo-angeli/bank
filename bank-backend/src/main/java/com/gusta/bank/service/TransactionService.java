package com.gusta.bank.service;

import com.gusta.bank.domain.dto.TransactionDTO;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionService {
    private final BankAccountRepository repository;
    private final JwtTokenProvider jwtTokenService;

    public void deposit(TransactionDTO transactionDTO) {
        if (!repository.existsByUser_Email(transactionDTO.getTo())) {
            log.error("Account with email {} don't exists", transactionDTO.getTo());
            throw new IllegalArgumentException("Invalid email!");
        }

        repository.sumValueByEmail(transactionDTO.getTo(), transactionDTO.getValue());
    }

    public void transfer(String token, TransactionDTO transactionDTO) {
        String from = jwtTokenService.getSubject(token);

        if (!repository.existsByUser_Email(transactionDTO.getTo())) {
            log.error("Invalid email!");
            throw new IllegalArgumentException("Account with email " + transactionDTO.getTo() + " don't exists");
        }
        if (repository.valueGreaterThanAccountBalance(from, transactionDTO.getValue())) {
            log.error("Insufficient balance");
            throw new IllegalArgumentException("Account with email " + from + " don't have " + transactionDTO.getValue() + "$ to transfer");
        }

        repository.subtractValueByEmail(from, transactionDTO.getValue());
        repository.sumValueByEmail(transactionDTO.getTo(), transactionDTO.getValue());
    }
}

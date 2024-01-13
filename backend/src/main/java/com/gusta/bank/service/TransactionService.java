package com.gusta.bank.service;

import com.gusta.bank.domain.dto.TransactionDTO;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionService {
    private final BankAccountRepository repository;
    private final JwtTokenProvider tokenProvider;

    public void deposit(TransactionDTO transactionDTO) {
        if (transactionDTO.getValue() <= 0) {
            log.error("Value {} equals or less than 0", transactionDTO.getValue());
            throw new IllegalArgumentException("Invalid value");
        }
        if (!repository.existsByUser_Email(transactionDTO.getTo())) {
            log.error("Account with email {} don't exists", transactionDTO.getTo());
            throw new IllegalArgumentException("Invalid email!");
        }

        repository.sumValueByEmail(transactionDTO.getTo(), transactionDTO.getValue());
    }

    public void transfer(String token, TransactionDTO transactionDTO) {
        if (transactionDTO.getValue() <= 0) {
            log.error("Value {} is equals or less than 0", transactionDTO.getValue());
            throw new IllegalArgumentException("Invalid value");
        }
        if (!repository.existsByUser_Email(transactionDTO.getTo())) {
            log.error("Invalid email!");
            throw new IllegalArgumentException("Account with email " + transactionDTO.getTo() + " don't exists");
        }

        String from = tokenProvider.getSubject(token);

        if (repository.valueGreaterThanAccountBalance(from, transactionDTO.getValue())) {
            log.error("Account with email {} don't have {} $ to transfer", from, transactionDTO.getValue());
            throw new IllegalArgumentException("Insufficient balance");
        }

        repository.subtractValueByEmail(from, transactionDTO.getValue());
        repository.sumValueByEmail(transactionDTO.getTo(), transactionDTO.getValue());
    }
}

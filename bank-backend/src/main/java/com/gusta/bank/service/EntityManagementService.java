package com.gusta.bank.service;

import com.gusta.bank.domain.model.BankAccount;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.security.domain.enums.Role;
import com.gusta.bank.security.domain.model.User;
import com.gusta.bank.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EntityManagementService {
    private final BankAccountRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public void create(UserDTO userDTO) {
        if (repository.existsByUser_Email(userDTO.getEmail())) {
            log.error("User with email {} already exists", userDTO.getEmail());
            throw new IllegalArgumentException("The email " + userDTO.getEmail() + " is already in use. Please choose another email");
        }

        User user = new User(null, userDTO.getUsername(), userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()), Role.ROLE_USER);

        repository.save(new BankAccount(null, user, 0));
    }

    public void deleteByToken(String token) {
        String userEmail = tokenProvider.getSubject(token);

        repository.deleteByUser_Email(userEmail);
    }

    public void deleteById(String id) {
        repository.deleteById(UUID.fromString(id));
    }
}

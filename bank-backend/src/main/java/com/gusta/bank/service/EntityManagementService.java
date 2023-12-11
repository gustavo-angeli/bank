package com.gusta.bank.service;

import com.gusta.bank.domain.model.BankAccount;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.security.domain.model.User;
import com.gusta.bank.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EntityManagementService {
    private final BankAccountRepository bankAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenService;

    public void create(UserDTO userDTO) {
        if (bankAccountRepository.existsByUser_Email(userDTO.getEmail())) {
            log.error("User with email {} already exists", userDTO.getEmail());
            throw new IllegalArgumentException("The email " + userDTO.getEmail() + " is already in use. Please choose another email");
        }

        User user = new User(null, userDTO.getUsername(), userDTO.getEmail(), passwordEncoder.encode(userDTO.getPassword()), userDTO.getRole());

        bankAccountRepository.save(new BankAccount(null, user, new BigDecimal(0)));
    }

    public void deleteByToken(String token) {
        String userEmail = jwtTokenService.getSubject(token);

        bankAccountRepository.deleteByUser_Email(userEmail);
    }

    public void deleteById(String id) {
        bankAccountRepository.deleteById(UUID.fromString(id));
    }


}

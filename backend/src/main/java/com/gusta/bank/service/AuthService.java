package com.gusta.bank.service;

import com.gusta.bank.security.domain.dto.TokenDTO;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.security.jwt.JwtTokenProvider;
import com.gusta.bank.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    public TokenDTO login(UserDTO userDTO) {
        String userEmail = userDTO.getEmail();
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(userEmail, userDTO.getPassword()));
        } catch (BadCredentialsException e) {
            log.error("Invalid email/password supplied for user {}", userDTO.getEmail(), e);
            throw e;
        }

        String role = repository.findRoleByUserEmail(userEmail).toString();

        return tokenProvider.createTokenDTO(userEmail, List.of(role));
    }

    public TokenDTO refreshToken(String token) {
        return tokenProvider.refreshTokenDTO(token);
    }
}

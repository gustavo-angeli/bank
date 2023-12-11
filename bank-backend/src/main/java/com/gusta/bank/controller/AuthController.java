package com.gusta.bank.controller;

import com.gusta.bank.security.domain.dto.TokenDTO;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ParameterValidation;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO userDTO) {
        ParameterValidation.nullOrEmptyParam(userDTO.getEmail(), userDTO.getPassword());
        return new ResponseEntity<>(service.login(userDTO), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<TokenDTO> refresh(@RequestHeader(value = "Authorization") String token) {
        ParameterValidation.nullOrEmptyParamCustomMessage(token, "Null or Empty Authorization Header");
        return new ResponseEntity<>(service.refreshToken(token), HttpStatus.OK);
    }
}

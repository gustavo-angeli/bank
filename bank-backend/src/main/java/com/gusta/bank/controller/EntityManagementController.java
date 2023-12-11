package com.gusta.bank.controller;

import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.service.EntityManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ParameterValidation;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/management")
@RequiredArgsConstructor
public class EntityManagementController {
    private final EntityManagementService service;

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody UserDTO userDTO) {
        ParameterValidation.nullOrEmptyParam(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());
        service.create(userDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@RequestHeader(value = "Authorization") String token) {
        ParameterValidation.nullOrEmptyParam(token);
        System.out.println(UUID.randomUUID());
        System.out.println(UUID.randomUUID());

        service.deleteByToken(token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ENDPOINTS THAT ONLY USERS WITH ROLE_ADMIN CAN ACCESS
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable(value = "id") String id) {
        ParameterValidation.nullOrEmptyParam(id);
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

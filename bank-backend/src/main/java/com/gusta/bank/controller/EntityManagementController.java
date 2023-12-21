package com.gusta.bank.controller;

import com.gusta.bank.security.domain.dto.TokenDTO;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.service.EntityManagementService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ParameterValidation;

import java.util.UUID;

@Tag(name = "Management", description = "Management endpoints")
@RestController
@RequestMapping(value = "/api/v1/management")
@RequiredArgsConstructor
public class EntityManagementController {
    private final EntityManagementService service;

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class),
                    examples = @ExampleObject(
                            name = "example",
                            value = "{\"username\": \"example\", \"email\":\"example@example.com\", \"password\":\"1234\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "204",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )
    )
    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody UserDTO userDTO) {
        ParameterValidation.nullOrEmptyParam(userDTO.getUsername(), userDTO.getEmail(), userDTO.getPassword());
        service.create(userDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiResponse(
            responseCode = "204",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )
    )
    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@RequestHeader(value = "Authorization") String token) {
        ParameterValidation.nullOrEmptyParam(token);
        service.deleteByToken(token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //ENDPOINTS THAT ONLY USERS WITH ROLE_ADMIN CAN ACCESS
    @ApiResponse(
            responseCode = "204",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable(value = "id") String id) {
        ParameterValidation.nullOrEmptyParam(id);
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

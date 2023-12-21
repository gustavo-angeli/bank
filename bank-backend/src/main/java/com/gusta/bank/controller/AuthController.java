package com.gusta.bank.controller;

import com.gusta.bank.security.domain.dto.TokenDTO;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.service.AuthService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ParameterValidation;

@Tag(name = "Authentication", description = "Authentication endpoints")
@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserDTO.class),
                    examples = @ExampleObject(
                            name = "example",
                            value = "{\"email\":\"admin@admin.com\", \"password\":\"1234\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TokenDTO.class)
            )
    )
    @PostMapping
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO userDTO) {
        ParameterValidation.nullOrEmptyParam(userDTO.getEmail(), userDTO.getPassword());
        return new ResponseEntity<>(service.login(userDTO), HttpStatus.OK);
    }

    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TokenDTO.class)
            )
    )
    @PutMapping
    public ResponseEntity<TokenDTO> refresh(@RequestHeader(value = "Authorization") String token) {
        return new ResponseEntity<>(service.refreshToken(token), HttpStatus.OK);
    }
}

package com.gusta.bank.security.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String username;
    private Boolean authenticated;
    private String token;
    private String refreshToken;
}

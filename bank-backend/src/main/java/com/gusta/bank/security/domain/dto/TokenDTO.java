package com.gusta.bank.security.domain.dto;


import lombok.Data;

@Data
public class TokenDTO {
    private String username;
    private Boolean authenticated;
    private String token;
    private String refreshToken;

    public TokenDTO() {
    }

    public TokenDTO(String username, Boolean authenticated, String token, String refreshToken) {
        this.username = username;
        this.authenticated = authenticated;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}

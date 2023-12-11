package com.gusta.bank.security.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gusta.bank.security.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private UUID uuid;
    private String username;
    private String email;
    private String password;
    private Role role;
}

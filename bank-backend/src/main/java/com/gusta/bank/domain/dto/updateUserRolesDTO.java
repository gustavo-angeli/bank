package com.gusta.bank.domain.dto;

import com.gusta.bank.security.domain.enums.Role;

public record updateUserRolesDTO(String userEmail, Role role) {
}

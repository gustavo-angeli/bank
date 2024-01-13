package com.gusta.bank.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.gusta.bank.security.domain.dto.TokenDTO;
import com.gusta.bank.security.domain.enums.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public class JwtTokenProviderTest {
    private final UserDetailsService userDetailsService = Mockito.mock(username -> "user");
    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(userDetailsService);

    private final String user = "user";
    private final List<String> roles = List.of(Role.ROLE_USER.toString());
    private final String invalidToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImV4cCI6MTcwMjc0NDA0OSwiaWF0IjoxNzAyNzQ0MDQ5fQ.0vnWwx8OLzReyISK5gU9iWZzx8U1AT7CNOi7UzbgZtpsYaJ7EOU3ByXQb4o_0VmMNDaRus37S9T16EhbkF0TSw";


    @Test
    void Should_ReturnTokenDTO_When_UsernameAndListOfRoles() {
        TokenDTO resultToken = tokenProvider.createTokenDTO(user, roles);

        Assertions.assertNotNull(resultToken);
    }

    @Test
    void Should_ReturnTokenDTO_When_ValidRefreshJwt() {
        String refreshToken = "Bearer " + tokenProvider.createTokenDTO(user, roles).getRefreshToken();

        TokenDTO resultToken = tokenProvider.refreshTokenDTO(refreshToken);

        Assertions.assertNotNull(resultToken);
    }
    @Test
    void Should_ThrowTokenExpiredException_When_InvalidRefreshJwt() {
        Assertions.assertThrows(JWTVerificationException.class, () -> tokenProvider.refreshTokenDTO(invalidToken));
    }

    @Test
    void Should_False_When_InvalidToken() {
        Assertions.assertFalse(tokenProvider.validateToken(invalidToken));
    }
    @Test
    void Should_True_When_TokenIsValid() {
        String token = tokenProvider.createTokenDTO(user, roles).getToken();

        Assertions.assertTrue(tokenProvider.validateToken(token));
    }
}

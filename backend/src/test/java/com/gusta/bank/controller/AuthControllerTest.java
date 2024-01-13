package com.gusta.bank.controller;

import com.gusta.bank.domain.model.BankAccount;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.domain.dto.TokenDTO;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.security.domain.enums.Role;
import com.gusta.bank.security.domain.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {
    private User user = new User(null, "user", "user@email.com", new BCryptPasswordEncoder().encode("1234"), Role.ROLE_USER);
    private BankAccount bankAccount = new BankAccount(null, user, 0);

    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private BankAccountRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        repository.deleteAll();
        user = new User(null, "user", "user@email.com", new BCryptPasswordEncoder().encode("1234"), Role.ROLE_USER);
        bankAccount = new BankAccount(null, user, 0);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void Should_ReturnHttpStatusBadRequest400_When_RequestBodyHasInvalidRequiredParams(String params) {
        UserDTO request = new UserDTO(null, params, params, params, null);

        RestAssured.given()
                .contentType(ContentType.JSON).body(request)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    void Should_ReturnHttp400_When_InvalidCredentials() {
        repository.save(bankAccount);

        UserDTO request = new UserDTO(null, null, "user@email.com", "12345", null);

        RestAssured.given()
                .contentType(ContentType.JSON).body(request)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }
    @Test
    void Should_ReturnHttpStatus204_When_ValidEmailAndPassword() {
        repository.save(bankAccount);
        UserDTO request = new UserDTO(null, null, "user@email.com", "1234", null);

        TokenDTO loginResponse = RestAssured.given()
                .contentType(ContentType.JSON).body(request)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class);


        Assertions.assertNotNull(loginResponse);
        Assertions.assertNotNull(loginResponse.getUsername());
        Assertions.assertNotNull(loginResponse.getToken());
        Assertions.assertNotNull(loginResponse.getRefreshToken());
        Assertions.assertTrue(loginResponse.getAuthenticated());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImV4cCI6MTcwMjc0NDA0OSwiaWF0IjoxNzAyNzQ0MDQ5fQ.0vnWwx8OLzReyISK5gU9iWZzx8U1AT7CNOi7UzbgZtpsYaJ7EOU3ByXQb4o_0VmMNDaRus37S9T16EhbkF0TSw"})
    void Should_ReturnHttpStatus401_When_InvalidToken(String token) {
        RestAssured.given()
                .contentType(ContentType.JSON).header("Authorization", "Bearer " + token)
                .when().put("/api/v1/auth")
                .then().statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void Should_ReturnHttpStatus204_When_ValidToken() {
        repository.save(bankAccount);
        UserDTO request = new UserDTO(null, "user", "user@email.com", "1234", null);

        TokenDTO loginResponse = RestAssured.given()
                .contentType(ContentType.JSON).body(request)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class);

        TokenDTO refreshTokenResponse = RestAssured.given()
                .contentType(ContentType.JSON).header("Authorization", "Bearer " + loginResponse.getRefreshToken())
                .when().put("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class);

        Assertions.assertNotNull(refreshTokenResponse);
        Assertions.assertNotNull(refreshTokenResponse.getUsername());
        Assertions.assertNotNull(refreshTokenResponse.getToken());
        Assertions.assertNotNull(refreshTokenResponse.getRefreshToken());
        Assertions.assertTrue(refreshTokenResponse.getAuthenticated());
    }
}

package com.gusta.bank.controller;

import com.gusta.bank.domain.model.BankAccount;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.domain.dto.TokenDTO;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.security.domain.enums.Role;
import com.gusta.bank.security.domain.model.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class EntityManagementControllerTest {
    @LocalServerPort
    private Integer port;

    private User user = new User(null, "user", "user@email.com", new BCryptPasswordEncoder().encode("1234"), Role.ROLE_USER);
    private BankAccount bankAccount = new BankAccount(null, user, 0);

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
                .when().post("/api/v1/management")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    void Should_ReturnHttpCreated204_When_ValidRequestBody() {
        UserDTO request = new UserDTO(null, "user", "user@email.com", "1234", null);

        RestAssured.given()
                .contentType(ContentType.JSON).body(request)
                .when().post("/api/v1/management")
                .then().statusCode(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void Should_ReturnHttpStatusBadRequest400_When_InvalidAuthorizationHeader(String header) {
        RestAssured.given()
                .header("Authorization", header)
                .when().delete("/api/v1/management")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    void Should_ReturnHttpStatus204_When_ValidAuthorizationHeaderAndValidToken() {
        repository.save(bankAccount);
        UserDTO request = new UserDTO(null, "user", "user@email.com", "1234", null);

        String loginResponseToken = RestAssured.given()
                .contentType(ContentType.JSON).body(request)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class).getToken();

        RestAssured.given()
                .header("Authorization", "Bearer " + loginResponseToken)
                .when().delete("/api/v1/management")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }
}

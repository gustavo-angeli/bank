package com.gusta.bank.controller;

import com.gusta.bank.domain.dto.TransactionDTO;
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
public class TransactionControllerTest {
    private UserDTO userDTO = new UserDTO(null, "user", "user@email.com", "1234", null);
    private TransactionDTO transactionRequest = new TransactionDTO("user", 10);
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
        user = new User(null, "user", "user@email.com", new BCryptPasswordEncoder().encode("1234"), Role.ROLE_USER);
        bankAccount = new BankAccount(null, user, 0);
        transactionRequest = new TransactionDTO("user@email.com", 10);
        repository.deleteAll();
    }

    //Deposit
    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void Should_ReturnHttpStatus400_When_ValueEqualsOrLessThanZero(String value) {
        repository.save(bankAccount);

        transactionRequest.setTo(" ");
        transactionRequest.setValue(Double.parseDouble(value));


        RestAssured.given()
                .contentType(ContentType.JSON).body(transactionRequest)
                .when().patch("/api/v1/transaction/deposit")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    void Should_ReturnHttpStatus400_When_NonExistentAccount() {
        RestAssured.given()
                .contentType(ContentType.JSON).body(transactionRequest)
                .when().patch("/api/v1/transaction/deposit")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    void Should_ReturnHttpStatus204_When_ExistentAccountAndValueGreaterThanZero() {
        repository.save(bankAccount);

        RestAssured.given()
                .contentType(ContentType.JSON).body(transactionRequest)
                .when().patch("/api/v1/transaction/deposit")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }

    //Transfer
    @Test
    void Should_ReturnHttpStatus400_When_InvalidAttributes() {
        TokenDTO loginToken = RestAssured.given()
                .contentType(ContentType.JSON).body(userDTO)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class);

        RestAssured.given()
                .contentType(ContentType.JSON).header("Authorization", "Bearer " + loginToken.getToken()).body(transactionRequest)
                .when().patch("/api/v1/transaction/transfer")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void Should_ReturnHttpStatus400_When_ValueEqualsOrLessThanZero_Transfer(String value) {
        TokenDTO loginToken = RestAssured.given()
                .contentType(ContentType.JSON).body(userDTO)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class);

        transactionRequest.setValue(Double.parseDouble(value));

        RestAssured.given()
                .contentType(ContentType.JSON).header("Authorization", "Bearer " + loginToken.getToken()).body(transactionRequest)
                .when().patch("/api/v1/transaction/transfer")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
    @Test
    void Should_ReturnHttpStatus400_When_NonExistentAccount_Transfer() {
        TokenDTO loginToken = RestAssured.given()
                .contentType(ContentType.JSON).body(userDTO)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class);

        RestAssured.given()
                .contentType(ContentType.JSON).header("Authorization", "Bearer " + loginToken.getToken()).body(transactionRequest)
                .when().patch("/api/v1/transaction/transfer")
                .then().statusCode(HttpStatus.BAD_REQUEST.value());
    }
//    @Test
//    void Should_ReturnHttpStatus400_When_InsufficientBalance() {
//        bankAccount.setUser(new User(null, "user2", "user2@email.com", "1234", Role.ROLE_USER));
//        repository.save(bankAccount);
//
//        transactionRequest.setTo("user2@email.com");
//        transactionRequest.setValue(10000);
//
//        String loginResponseToken = RestAssured.given()
//                .contentType(ContentType.JSON).body(userDTO)
//                .when().post("/api/v1/auth")
//                .then().statusCode(HttpStatus.OK.value())
//                .extract().as(TokenDTO.class).getToken();
//
//        RestAssured.given()
//                .contentType(ContentType.JSON)
//                .header("Authorization", "Bearer " + loginResponseToken)
//                .body(transactionRequest)
//                .when().patch("/api/v1/transaction/transfer")
//                .then().statusCode(HttpStatus.BAD_REQUEST.value());
//    }
    @Test
    void Should_ReturnHttpStatus204_When_ValidAuthorizationHeaderExistentUserAndValueGreaterThanZero() {
        bankAccount.setBalance(100);
        repository.save(bankAccount);

        bankAccount.setUser(new User(null, "user2", "user2@email.com", "1234", Role.ROLE_USER));
        repository.save(bankAccount);

        transactionRequest.setTo("user2@email.com");

        String loginResponseToken = RestAssured.given()
                .contentType(ContentType.JSON).body(userDTO)
                .when().post("/api/v1/auth")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(TokenDTO.class).getToken();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + loginResponseToken)
                .body(transactionRequest)
                .when().patch("/api/v1/transaction/transfer")
                .then().statusCode(HttpStatus.NO_CONTENT.value());
    }
}

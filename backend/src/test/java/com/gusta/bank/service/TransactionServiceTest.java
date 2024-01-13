package com.gusta.bank.service;

import com.gusta.bank.domain.dto.TransactionDTO;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceTest {
    private final BankAccountRepository repository = Mockito.mock(BankAccountRepository.class);
    private final JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
    private final TransactionService service = new TransactionService(repository, tokenProvider);

    private TransactionDTO transactionDTO = new TransactionDTO("email@email.com", 10);

    @BeforeEach
    public void setup() {
        Mockito.reset(repository, tokenProvider);
        transactionDTO = new TransactionDTO("email@email.com", 10);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void deposit_Fail_ValueEqualsOrLessThanZero(int value) {
        transactionDTO.setValue(value);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deposit(transactionDTO));
    }
    @Test
    void deposit_Fail_NonExistentEmail() {
        Mockito.when(repository.existsByUser_Email(Mockito.anyString())).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deposit(transactionDTO));
    }
    @Test
    void deposit_Success_ExistentEmail() {
        Mockito.when(repository.existsByUser_Email(Mockito.anyString())).thenReturn(true);

        service.deposit(transactionDTO);

        Mockito.verify(repository).sumValueByEmail(transactionDTO.getTo(), transactionDTO.getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void transfer_Fail_ValueEqualsOrLessThanZero(int value) {
        transactionDTO.setValue(value);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deposit(transactionDTO));
    }
    @Test
    void transfer_Fail_NonExistentEmail() {
        Mockito.when(repository.existsByUser_Email(Mockito.anyString())).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deposit(transactionDTO));
    }
    @Test
    void transfer_Fail_ValueToTransferGreaterThanBalance() {
        String fromEmail = "fromEmail@email.com";
        Mockito.when(tokenProvider.getSubject(Mockito.anyString())).thenReturn(fromEmail);
        Mockito.when(repository.existsByUser_Email(transactionDTO.getTo())).thenReturn(true);
        Mockito.when(repository.valueGreaterThanAccountBalance(fromEmail, transactionDTO.getValue())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.transfer("token jwt", transactionDTO));
    }
    @Test
    void transfer_Success_ExistentEmail() {
        String fromEmail = "fromEmail@email.com";
        Mockito.when(tokenProvider.getSubject(Mockito.anyString())).thenReturn(fromEmail);
        Mockito.when(repository.existsByUser_Email(Mockito.anyString())).thenReturn(true);
        Mockito.when(repository.valueGreaterThanAccountBalance(fromEmail, transactionDTO.getValue())).thenReturn(false);


        service.transfer("token jwt", transactionDTO);

        Mockito.verify(repository).subtractValueByEmail(fromEmail, transactionDTO.getValue());
        Mockito.verify(repository).sumValueByEmail(transactionDTO.getTo(), transactionDTO.getValue());
    }
}

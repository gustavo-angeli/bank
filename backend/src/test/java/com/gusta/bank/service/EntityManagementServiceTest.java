package com.gusta.bank.service;

import com.gusta.bank.domain.model.BankAccount;
import com.gusta.bank.repository.BankAccountRepository;
import com.gusta.bank.security.domain.dto.UserDTO;
import com.gusta.bank.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EntityManagementServiceTest {
    private final BankAccountRepository repository = Mockito.mock(BankAccountRepository.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtTokenProvider tokenProvider = Mockito.mock(JwtTokenProvider.class);
    private final EntityManagementService service = new EntityManagementService(repository, passwordEncoder, tokenProvider);

    private UserDTO userDTO = new UserDTO(null, "username", "userEmail@email.com", "1234", null);

    @BeforeEach
    public void setup() {
        Mockito.reset(repository, tokenProvider);
        userDTO = new UserDTO(null, "username", "userEmail@email.com", "1234", null);
    }

    @Test
    void create_Fail_ExistentUserWithEmail() {
        Mockito.when(repository.existsByUser_Email(userDTO.getEmail())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.create(userDTO));
    }
    @Test
    void create_Success() {
        Mockito.when(repository.existsByUser_Email(userDTO.getEmail())).thenReturn(false);

        service.create(userDTO);

        Mockito.verify(repository).save(Mockito.any(BankAccount.class));
    }

    @Test
    void deleteByToken() {
        String userExtractedByToken = "userEmail@email.com";
        Mockito.when(tokenProvider.getSubject(Mockito.anyString())).thenReturn(userExtractedByToken);

        service.deleteByToken("token jwt");

        Mockito.verify(repository).deleteByUser_Email(userExtractedByToken);
    }

    @Test
    void deleteById() {
        UUID id = UUID.randomUUID();

        service.deleteById(id.toString());

        Mockito.verify(repository).deleteById(id);
    }
}

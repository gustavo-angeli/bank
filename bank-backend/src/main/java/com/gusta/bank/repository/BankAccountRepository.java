package com.gusta.bank.repository;

import com.gusta.bank.domain.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    Optional<BankAccount> findByUser_Email(String email);

    boolean existsByUser_Email(String userEmail);

    void deleteByUser_Email(String userEmail);

    @Modifying
    @Query("UPDATE BankAccount b SET b.balance = b.balance + :value WHERE b.user.email = :email")
    void sumValueByEmail(@Param("email") String email, @Param("value") BigDecimal value);

    @Modifying
    @Query("UPDATE BankAccount b SET b.balance = b.balance - :value WHERE b.user.email = :email")
    void subtractValueByEmail(@Param("email") String userEmail, @Param("value") BigDecimal value);


    @Query("select count(b) > 0 from BankAccount b where b.user.email = :email AND b.balance < :value")
    boolean valueGreaterThanAccountBalance(@Param("email") String userEmail, @Param("value") BigDecimal value);
}

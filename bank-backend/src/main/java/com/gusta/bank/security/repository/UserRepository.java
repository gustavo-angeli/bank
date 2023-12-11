package com.gusta.bank.security.repository;

import com.gusta.bank.security.domain.enums.Role;
import com.gusta.bank.security.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String userEmail);

    Optional<User> findByEmail(final String email);

    void deleteByEmail(String email);

    @Query("SELECT u.role FROM User u WHERE u.email = :email")
    Role findRoleByUserEmail(@Param("email") String email);
}

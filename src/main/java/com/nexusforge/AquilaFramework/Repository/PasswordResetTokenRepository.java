package com.nexusforge.AquilaFramework.Repository;

import com.nexusforge.AquilaFramework.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmailAndToken(String email, String token);
    void deleteByEmail(String email);
}


package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.PasswordResetToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends ListCrudRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(LocalUser user);
}

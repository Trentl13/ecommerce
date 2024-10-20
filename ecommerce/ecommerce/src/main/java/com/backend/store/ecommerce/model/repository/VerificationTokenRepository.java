package com.backend.store.ecommerce.model.repository;

import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);
}

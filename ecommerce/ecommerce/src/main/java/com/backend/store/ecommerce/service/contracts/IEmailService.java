package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.exception.EmailFailureException;
import com.backend.store.ecommerce.model.PasswordResetToken;
import com.backend.store.ecommerce.model.VerificationToken;

public interface IEmailService {
    void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException;
    void sendPasswordResetEmail(PasswordResetToken token) throws EmailFailureException;
}
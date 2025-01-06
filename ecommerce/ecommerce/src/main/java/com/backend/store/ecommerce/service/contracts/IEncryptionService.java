package com.backend.store.ecommerce.service.contracts;

public interface IEncryptionService {
    String encryptPassword(String password);
    boolean verifyPassword(String password, String hash);
}
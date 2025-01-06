package com.backend.store.ecommerce.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService implements com.backend.store.ecommerce.service.contracts.IEncryptionService {
    @Value("${encryption.salt.rounds}")
    //This annotation is used to inject a configuration value (encryption.salt.rounds) from an external property file
    private int saltRounds; //saltRounds is an integer representing the "cost" factor (or the number of hashing rounds) for generating the salt.
    private String salt; //This is a private instance variable that will hold the generated salt string after it’s created by the BCrypt algorithm.

    @PostConstruct //показва че метода трябва да се ползва след като bean-a е готов
    public void saltConstruct() {
        salt = BCrypt.gensalt(saltRounds);
    }

    public String encryptPassword(String password) {
        return BCrypt.hashpw(password, salt);
    }

    public boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}

//The salt is used as part of the hashing process to make passwords harder to crack by adding random data
//The salt is generated and stored in the salt variable as soon as the class is initialized.

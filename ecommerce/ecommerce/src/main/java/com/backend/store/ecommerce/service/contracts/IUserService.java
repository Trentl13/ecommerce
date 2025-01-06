package com.backend.store.ecommerce.service.contracts;

import com.backend.store.ecommerce.api.model.*;
import com.backend.store.ecommerce.exception.*;
import com.backend.store.ecommerce.model.LocalUser;

public interface IUserService {
    LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException;
    String logInUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException;
    boolean verifyUser(String token);
    void passwordResetEmail(EmailBody emailBody) throws EmailFailureException;
    void changePassword(String token, PasswordChangeBody body) throws PasswordFailureException, MissingTokenException;
    void insertAddress(AddressBody body) throws AddressFailureExeption;
    void updateAddress(AddressUpdateBody body) throws AddressFailureExeption;
}
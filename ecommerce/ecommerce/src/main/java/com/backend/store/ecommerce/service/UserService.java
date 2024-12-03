package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.api.model.*;
import com.backend.store.ecommerce.exception.*;
import com.backend.store.ecommerce.model.Address;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.PasswordResetToken;
import com.backend.store.ecommerce.model.VerificationToken;
import com.backend.store.ecommerce.model.repository.AddressRepository;
import com.backend.store.ecommerce.model.repository.LocalUserRepository;
import com.backend.store.ecommerce.model.repository.PasswordResetTokenRepository;
import com.backend.store.ecommerce.model.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private LocalUserRepository localUserRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private AddressRepository addressRepository;

    public UserService(LocalUserRepository localUserRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       EncryptionService encryptionService,
                       JWTService jwtService,
                       EmailService emailService,
                       AddressRepository addressRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository){
        this.localUserRepository = localUserRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.addressRepository = addressRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        if (localUserRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
                || localUserRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()){
           throw new UserAlreadyExistsException();
        }
        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setUsername(registrationBody.getUsername());
        user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(user);
        emailService.sendVerificationEmail(verificationToken);
        //verificationTokenRepository.save(verificationToken); не ни трябва заради CascadeType.ALL в LocalUser//токена се запазва заедно със запазването на LocalUser

        return localUserRepository.save(user);
    }

    private VerificationToken createVerificationToken(LocalUser user){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtService.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }
    private PasswordResetToken createPasswordResetToken(LocalUser user){
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(jwtService.generateVerificationJWT(user));
        passwordResetToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        passwordResetToken.setUser(user);
        user.getPasswordResetTokens().add(passwordResetToken);
        return passwordResetToken;
    }

    public String logInUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(loginBody.getUsername());
        if(opUser.isPresent()){
            LocalUser user = opUser.get(); //checks if the user exists
            if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())){ //verifies password
                if(user.isEmailVerified()) { //verifies email
                    return jwtService.generateJWT(user);
                }
                else { //ако имейла не е потвърден взима листа с токените на usera и проверя дали някога е получавал токен или дали е получавал преди по-малко от час и ако тези двете кондиции са верни изпраща токен на usera
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.isEmpty() || verificationTokens.get(0).getCreatedTimestamp()
                            .before(new Timestamp(System.currentTimeMillis() - (60*1000)));//означава ако няма токени изпратени или последния изпратен е бил преди timestampa да изпрати нов
                    if(resend){
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenRepository.save(verificationToken);
                        emailService.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
            }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token){
        Optional<VerificationToken> opToken = verificationTokenRepository.findByToken(token);//търсим дали сме изпратили ние токена
        if(opToken.isPresent()){
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();//взимаме usera от токена и проверяваме дали е verified или не, ако не е го запазваме и verify-ваме и после трием токените свързани с него понеже не ни трябват
            if(!user.isEmailVerified()){
                user.setEmailVerified(true);
                localUserRepository.save(user);
                verificationTokenRepository.deleteByUser(user);
                return true;
            }
        }
        return false;
    }
    public void passwordResetEmail(EmailBody emailBody) throws EmailFailureException {
        Optional<LocalUser> opUser = localUserRepository.findByEmailIgnoreCase(emailBody.email);

        if (opUser.isEmpty()) {
            throw new EmailFailureException("No such user exists with given email");
        }

        LocalUser user = opUser.get();
        PasswordResetToken prToken = createPasswordResetToken(user);
        passwordResetTokenRepository.save(prToken);
        emailService.sendPasswordResetEmail(prToken);
    }
    public void changePassword(String token, PasswordChangeBody body)
            throws PasswordFailureException, MissingTokenException {
        Optional<PasswordResetToken> opToken = passwordResetTokenRepository.findByToken(token);

        if (opToken.isEmpty()) {
            throw new MissingTokenException("Token is not valid");
        }

        LocalUser user = opToken.get().getUser();

        if (!body.getNewPassword1().equals(body.getNewPassword2())) {
            throw new PasswordFailureException("The given passwords don't match");
        }

        if (body.getNewPassword1().equals(user.getPassword())) {
            throw new PasswordFailureException("The new password is equal to the current one");
        }

        String encryptedPassword = encryptionService.encryptPassword(body.getNewPassword1());
        user.setPassword(encryptedPassword);
        localUserRepository.save(user);
        passwordResetTokenRepository.deleteByUser(user);
    }
    public void insertAddress(AddressBody body) throws AddressFailureExeption {

        Address addressToAdd = new Address(body.getAdressLine1(), body.getAdressLine2(), body.getCity(), body.getCountry());
        LocalUser user = localUserRepository.findById(body.getUserId())
                .orElseThrow(() -> new AddressFailureExeption("User not found"));
        addressToAdd.setUser(user);
        addressRepository.save(addressToAdd);
    }
    public void updateAddress(AddressUpdateBody body) throws AddressFailureExeption {
       Address address = addressRepository.findById(body.getId())
                .orElseThrow(() -> new AddressFailureExeption("Address not found"));
        address.setAdressLine1(body.getAdressLine1());
        address.setAdressLine2(body.getAdressLine2());
        address.setCity(body.getCity());
        address.setCountry(body.getCountry());

        addressRepository.save(address);
    }


}

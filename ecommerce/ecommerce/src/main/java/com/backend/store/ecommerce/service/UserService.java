package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.api.model.LoginBody;
import com.backend.store.ecommerce.api.model.RegistrationBody;
import com.backend.store.ecommerce.exception.EmailFailureException;
import com.backend.store.ecommerce.exception.UserAlreadyExistsException;
import com.backend.store.ecommerce.exception.UserNotVerifiedException;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.VerificationToken;
import com.backend.store.ecommerce.model.repository.LocalUserRepository;
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

    public UserService(LocalUserRepository localUserRepository, VerificationTokenRepository verificationTokenRepository, EncryptionService encryptionService, JWTService jwtService, EmailService emailService){
        this.localUserRepository = localUserRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.encryptionService = encryptionService;
        this.jwtService = jwtService;
        this.emailService = emailService;
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
                    boolean resend = verificationTokens.size() == 0 || verificationTokens.get(0).getCreatedTimestamp()
                            .before(new Timestamp(System.currentTimeMillis() -(60*60*1000)));//означава ако няма токени изпратени или последния изпратен е бил преди timestampa да изпрати нов
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

}

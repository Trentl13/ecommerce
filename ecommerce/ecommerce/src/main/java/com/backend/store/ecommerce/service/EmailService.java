package com.backend.store.ecommerce.service;

import com.backend.store.ecommerce.exception.EmailFailureException;
import com.backend.store.ecommerce.model.PasswordResetToken;
import com.backend.store.ecommerce.model.VerificationToken;
import com.backend.store.ecommerce.service.contracts.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {
    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;

    private JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    private SimpleMailMessage makeMailMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress);
        return simpleMailMessage;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(verificationToken.getUser().getEmail());
        message.setSubject("Verify your email to activate your account.");
        message.setText("Please folow the link below to verify your email for account activation.\n" + url + "/auth/verify?token=" + verificationToken.getToken());
        trySendEMessage(message);
    }
    public void sendPasswordResetEmail(PasswordResetToken token) throws EmailFailureException{
        SimpleMailMessage message = makeMailMessage();
        message.setTo(token.getUser().getEmail());
        message.setSubject("Reset your password");
        message.setText("Please follow the link below to reset the password for your account.\n"
                +url
                +"/auth/resetpassword?token="
                +token.getToken());
        trySendEMessage(message);
    }
    public void trySendEMessage(SimpleMailMessage message) throws EmailFailureException{
        try
        {
            javaMailSender.send(message);
        }
        catch(MailException ex)
        {
            throw new EmailFailureException(ex.getMessage());
        }
    }
}

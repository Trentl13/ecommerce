package com.backend.store.ecommerce.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.backend.store.ecommerce.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

//encrypted string JSON WEB TOKEN credential verifier-
// user държи по един unique токен, който всеки път когато поиска някакъв вид дата от фронт енда
// ще е закрепен с тях и след енкриптирането му
// ще могат да са закачат credential-ите към съответния user
//например когато на някои сайтове ти даде session expired това е защото токена е изтекъл и те log outva от акаунт и т.н.
@Service
public class JWTService {
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}") //Authentication server потвърждава кой е пуснал токена,на някои страници в гоогле пише че са опасни ако токена е изтекъл
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";

    @PostConstruct
    public void postConstruct(){
        algorithm = Algorithm.HMAC256(algorithmKey);//ще се използва за да направи токена tamper-proof
    }

    public String generateJWT(LocalUser user){
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())//This stores the username inside the token, allowing the receiving party (like an API) to know which user the token belongs to.
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000*expiryInSeconds)))
                .withIssuer(issuer)//This specifies the issuer of the token, which is typically the server or authority responsible for generating the JWT.
                .sign(algorithm);//This signs the JWT using the specified algorithm, making the token tamper-proof. The signature ensures that the token cannot be altered without invalidating it.
    }

    public String getUsernameKey(String token){
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

}

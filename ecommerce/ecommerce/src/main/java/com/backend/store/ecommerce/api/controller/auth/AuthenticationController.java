package com.backend.store.ecommerce.api.controller.auth;

import com.backend.store.ecommerce.api.model.LoginBody;
import com.backend.store.ecommerce.api.model.LoginResponse;
import com.backend.store.ecommerce.api.model.RegistrationBody;
import com.backend.store.ecommerce.exception.UserAlreadyExistsException;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserService userService;

    public AuthenticationController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody){ //@Valid проверява дали правилно е въведен имейл
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody){
        String jwt = userService.logInUser(loginBody);
        if(jwt == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else{
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            return ResponseEntity.ok(response);

        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user){
        return user;

    }
}

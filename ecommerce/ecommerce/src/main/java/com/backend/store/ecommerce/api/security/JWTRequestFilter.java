package com.backend.store.ecommerce.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.backend.store.ecommerce.model.LocalUser;
import com.backend.store.ecommerce.model.repository.LocalUserRepository;
import com.backend.store.ecommerce.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final LocalUserRepository localUserRepository;

    public JWTRequestFilter(JWTService jwtService, LocalUserRepository localUserRepository) {
        this.jwtService = jwtService;
        this.localUserRepository = localUserRepository;
    }

    //Every HTTP request passes through this filter before reaching the actual controller endpoints.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) { //checks that the Authorization header exists and starts with "Bearer ". The "Bearer " prefix is the standard way to include a token in the Authorization header in OAuth 2.0 and JWT.
            String token = tokenHeader.substring(7); //extracts the actual token from the Authorization header by removing the "Bearer " prefix.
            try {
                String username = jwtService.getUsernameKey(token);//decodes the JWT and extracts the username from it.
                Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(username);//After extracting the username from the token, the code uses the localUserRepository to fetch the user from the database using the username
                if (opUser.isPresent()) {
                    LocalUser user = opUser.get();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, new ArrayList()); //A UsernamePasswordAuthenticationToken is created to authenticate the user, null for credentials (since authentication was done via JWT).
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));//This line attaches additional details about the request (like the remote IP address or session ID) to the UsernamePasswordAuthenticationToken
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken); //Once the authentication token is created, the SecurityContextHolder stores the authentication object, effectively marking the user as authenticated for this request. This is the key step in allowing Spring Security to treat the user as logged in.
                }
            } catch (JWTDecodeException ex) {
            }

        }
        filterChain.doFilter(request, response); //After processing the JWT and setting up the authentication context, the request is passed along to the next filter in the chain or the controller

    }
}

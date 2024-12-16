package com.SaGa.Project.controller;

import com.SaGa.Project.jwtToken.JwtUtil;
import com.SaGa.Project.model.User;
import com.SaGa.Project.responce.TokenResponce;
import com.SaGa.Project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/authentication")
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    public LoginController(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponce> loginUser(@RequestBody User user){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            Optional<User> optionalUser = userService.findUserByEmail(user.getEmail());
            if (optionalUser.isEmpty()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponce(null, null, "Invalid Login Credintials"));
            }

            User authenticatedUser = optionalUser.get();

            if(!authenticatedUser.isEnabled()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponce(null, null, "Account not activated. Please verify your email."));
            }

            String token = jwtUtil.generateToken(authenticatedUser, authenticatedUser.getRoles());
            String userId = authenticatedUser.getUserId();

            return new ResponseEntity<>(new TokenResponce(token, userId, "User Authenticated Successfully"), HttpStatus.OK);
        }catch (AuthenticationException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponce(null, null,"Login Failed : "+ e.getMessage()));
        }
    }
}

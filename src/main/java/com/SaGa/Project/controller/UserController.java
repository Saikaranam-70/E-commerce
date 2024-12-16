package com.SaGa.Project.controller;

import com.SaGa.Project.EmailService.EmailService;
import com.SaGa.Project.exception.AdminAlreadyExistException;
import com.SaGa.Project.exception.UserNotFoundException;
import com.SaGa.Project.jwtToken.JwtUtil;
import com.SaGa.Project.model.User;
import com.SaGa.Project.responce.ConfirmationResponse;
import com.SaGa.Project.responce.UserResponce;
import com.SaGa.Project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired(required = true)
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;


    @PostMapping("/register")
    public ResponseEntity<UserResponce> registerUser(@RequestBody User user){
        Optional<User> optionalUser = userService.findUserByEmail(user.getEmail());
        if(!optionalUser.isPresent()){
            User newUser = userService.registerUser(user);

            String ConfirmationId = UUID.randomUUID().toString();
            userService.saveConfirmationId(newUser.getEmail(), ConfirmationId);
            emailService.sendConfirmationEmail(user.getEmail(), ConfirmationId);
//            boolean roles = userService.equals("user");
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponce("Please Verify Your Email", user.getEmail()));
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponce("User With email :"+ user.getEmail()+" already Exist", null));
        }
    }

    @PostMapping("/admin/register")
    public ResponseEntity<UserResponce> registerAsAdmin(@RequestBody User user) throws AdminAlreadyExistException {
        try{
            Optional<User> optionalUser = userService.findUserByEmail(user.getEmail());
            if(!optionalUser.isPresent()){
                User newUser = userService.registerAsAdmin(user);


                String ConfirmationId = UUID.randomUUID().toString();
                userService.saveConfirmationId(newUser.getEmail(), ConfirmationId);
                emailService.sendConfirmationEmail(user.getEmail(), ConfirmationId);

                return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponce("Admin Registered Successfully please Verify", user.getEmail()));
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponce("Email Already Exists", null));
            }
        }catch (AdminAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponce(e.getMessage(), null));
        }
    }

    @GetMapping("/getUser/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") String userId){
        Optional<User> existingUser = userService.getUserById(userId);
        if(existingUser.isPresent()){
            return new ResponseEntity<>(existingUser.get(), HttpStatus.FOUND);
        }else{
            return new ResponseEntity<>(existingUser.get(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmUser(@RequestParam String token){
        ConfirmationResponse result = userService.findUserByConfirmation(token);

        return new ResponseEntity<>(result.getMessage(), HttpStatus.FOUND);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<UserResponce> forgotPassword(@RequestParam String email){
        Optional<User> optionalUser = userService.findUserByEmail(email);


        if (optionalUser.isPresent()){
            String token = UUID.randomUUID().toString();
            userService.savePasswordResetToken(email, token);

            emailService.sendPasswordResetEmail(email, token);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new UserResponce("Password Reset Link sent ", email));

        }else {
            throw new UserNotFoundException("User not found ");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<UserResponce> resetPassword(@RequestParam String token, @RequestParam String newPassword){
        User result = userService.findUserByResetToken(token);

        if (result !=null){
            userService.updatePassword(result, newPassword);
            return ResponseEntity.status(HttpStatus.OK).body(new UserResponce("Password successfully updated", result.getEmail()));
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponce("Invalid or expired token", null));
        }
    }

}

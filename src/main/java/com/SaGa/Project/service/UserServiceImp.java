package com.SaGa.Project.service;

import com.SaGa.Project.exception.AdminAlreadyExistException;
import com.SaGa.Project.exception.UserNotFoundException;
import com.SaGa.Project.model.Address;
import com.SaGa.Project.model.User;
import com.SaGa.Project.repository.UserRepository;
import com.SaGa.Project.responce.ConfirmationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImp implements UserService{

    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private UserRepository userRepository;
    @Override
    public User registerUser(User user) {
        User optionalUser = new User();
        optionalUser.setEmail(user.getEmail());
        optionalUser.setName(user.getName());
        optionalUser.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<String> roles = Collections.singleton("user");
        optionalUser.setRoles(roles);
        optionalUser.setAddress(null);
        optionalUser.setPhone(user.getPhone());
        optionalUser.setOrders(null);
        optionalUser.setWishList(null);
        optionalUser.setCart(null);


        return userRepository.save(optionalUser);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public User registerAsAdmin(User user) throws AdminAlreadyExistException {
        boolean adminExists = userRepository.findByRolesContaining("ADMIN").isPresent();
        if(!adminExists) {
            User admin = new User();
            admin.setName(user.getName());
            admin.setEmail(user.getEmail());
            admin.setPhone(user.getPhone());
            admin.setPassword(passwordEncoder.encode(user.getPassword()));
            Set<String> roles = Collections.singleton("ADMIN");
            admin.setRoles(roles);

            return userRepository.save(admin);
        }else {
            throw new AdminAlreadyExistException("Admin Already Exists");
        }
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User updateUserAddress(String userId, Address address) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setAddress(address);
            return userRepository.save(user);
        }
        throw new UserNotFoundException("User Not Found With UserId :"+ userId);
    }

    @Override
    public Address getUserAddress(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isPresent()){
            return optionalUser.get().getAddress();
        }
        throw new UserNotFoundException("User not found with userId :"+userId);
    }

    @Override
    public String createConfirmationToken(User newUser) {
        return "";
    }

    @Override
    public void saveConfirmationId(String email, String confirmationId) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setConfirmationId(confirmationId);
            user.setEnabled(false);
            userRepository.save(user);
        }else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public ConfirmationResponse findUserByConfirmation(String token) {
        Optional<User> optionalUser = userRepository.findUserByConfirmationId(token);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setConfirmationId(null);
            userRepository.save(user);
            return new ConfirmationResponse("User successfully verified", HttpStatus.OK);
        }else {
            return new ConfirmationResponse("Invalid or expired token", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void savePasswordResetToken(String email, String token) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User existingUser = optionalUser.get();

            existingUser.setResetPasswordToken(token);
            existingUser.setTokenExpiryDate(LocalDateTime.now().plusHours(1));

            userRepository.save(existingUser);;
        }
    }

    @Override
    public User findUserByResetToken(String token) {
        Optional<User> user = userRepository.findUserByResetPasswordToken(token);

        if(user.isPresent() && user.get().getTokenExpiryDate().isAfter(LocalDateTime.now())){
            return user.get();
        }else {
            throw new UserNotFoundException("user not found");
        }

    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));

        user.setResetPasswordToken(null);
        user.setTokenExpiryDate(null);

        userRepository.save(user);

    }


}

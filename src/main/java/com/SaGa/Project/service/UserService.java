package com.SaGa.Project.service;

import com.SaGa.Project.exception.AdminAlreadyExistException;
import com.SaGa.Project.model.Address;
import com.SaGa.Project.model.User;
import com.SaGa.Project.responce.ConfirmationResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    public User registerUser(User user);

    Optional<User> findUserByEmail(String email);

    User registerAsAdmin(User user) throws AdminAlreadyExistException;

    Optional<User> getUserById(String userId);

    User updateUserAddress(String userId, Address address);

    Address getUserAddress(String userId);

    String createConfirmationToken(User newUser);

    void saveConfirmationId(String email, String confirmationId);

    ConfirmationResponse findUserByConfirmation(String token);

    void savePasswordResetToken(String email, String token);

    User findUserByResetToken(String token);

    void updatePassword(User user, String newPassword);
}

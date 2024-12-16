package com.SaGa.Project.repository;

import com.SaGa.Project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<Object> findByRolesContaining(String role);

    Optional<User> findUserByConfirmationId(String token);

    Optional<User> findUserByResetPasswordToken(String token);
}

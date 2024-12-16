package com.SaGa.Project.exception;

public class AdminAlreadyExistException extends Throwable {
    public AdminAlreadyExistException(String adminAlreadyExists) {
        super(adminAlreadyExists);
    }
}

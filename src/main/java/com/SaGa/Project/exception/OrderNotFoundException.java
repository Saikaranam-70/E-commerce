package com.SaGa.Project.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderNotFound) {
        super(orderNotFound);
    }
}

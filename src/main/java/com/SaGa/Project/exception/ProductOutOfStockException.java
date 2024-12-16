package com.SaGa.Project.exception;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(String msg){
        super(msg);
    }

}

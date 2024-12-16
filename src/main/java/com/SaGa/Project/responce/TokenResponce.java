package com.SaGa.Project.responce;

public class TokenResponce {
    private String message;
    private String token;
    private String userId;


    public TokenResponce(String token, String userId, String message){
        this.message = message;
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

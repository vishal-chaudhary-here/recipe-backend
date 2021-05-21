package com.mycompany.app.recipe.exception;

public class BadRequestException extends RuntimeException {
    private String message;
    private String reason;

    public BadRequestException(String reason, String message) {
        this.message = message;
        this.reason = reason;
    }

    public String getMessage() {
        return this.message;
    }

    public String getReason() {
        return this.reason;
    }    
}

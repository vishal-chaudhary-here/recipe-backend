package com.mycompany.app.recipe.exception;

public class NotFoundException extends RuntimeException {
    private String reason;
    private String message;
    public NotFoundException(String reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public String getReason() {
        return this.reason;
    }

    public String getMessage() {
        return this.message;
    }
    
    
}

package com.mycompany.app.recipe.exception;

public class ConflictException extends RuntimeException {
    private String conflictReason;
    private String conflictMessage;
    public ConflictException(String conflictReason, String conflictMessage) {
        this.conflictMessage = conflictMessage;
        this.conflictReason = conflictReason;
    }

    public String getConflictReason() {
        return this.conflictReason;
    }

    public String getConflictMessage() {
        return this.conflictMessage;
    }
    
}
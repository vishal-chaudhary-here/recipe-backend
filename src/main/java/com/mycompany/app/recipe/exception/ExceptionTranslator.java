package com.mycompany.app.recipe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.mycompany.app.recipe.web.api.model.Error;

@ControllerAdvice
public class ExceptionTranslator {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> handleBadRequestException(
        BadRequestException ex, WebRequest request) {

        Error error = new Error();
        error.setReason(ex.getReason());
        error.setMessage(ex.getMessage());
        error.setCode("ERR-01");
        error.setStatus("HttpStatus.BAD_REQUEST");

        return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Error> handleConflictException(
        ConflictException ex, WebRequest request) {

        Error error = new Error();
        error.setReason(ex.getConflictReason());
        error.setMessage(ex.getConflictMessage());
        error.setCode("ERR-02");
        error.setStatus("HttpStatus.CONFLICT");

        return new ResponseEntity<Error>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> handleRuntimeException(
        RuntimeException ex, WebRequest request) {

        Error error = new Error();
        error.setReason("Internal Server Error");
        error.setMessage("Something is Wrong");
        error.setCode("ERR-03");
        error.setStatus("HttpStatus.INTERNAL_SERVER_ERROR");

        return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Error> handleNotFoundException(
        NotFoundException ex, WebRequest request) {

        Error error = new Error();
        error.setReason(ex.getReason());
        error.setMessage(ex.getMessage());
        error.setCode("ERR-04");
        error.setStatus("HttpStatus.NOT_FOUND");

        return new ResponseEntity<Error>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(
        IllegalArgumentException ex, WebRequest request) {

        Error error = new Error();
        error.setReason("Illegal Argument");
        error.setMessage(ex.getMessage());
        error.setCode("ERR-05");
        error.setStatus("HttpStatus.NOT_FOUND");

        return new ResponseEntity<Error>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

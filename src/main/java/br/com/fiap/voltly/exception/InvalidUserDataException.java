package br.com.fiap.voltly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException(String message) {
        super(message);
    }
    
    public InvalidUserDataException(String field, String message) {
        super(String.format("Invalid user data for field %s: %s", field, message));
    }
} 
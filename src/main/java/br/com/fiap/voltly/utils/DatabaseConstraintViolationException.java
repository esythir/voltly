package br.com.fiap.voltly.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DatabaseConstraintViolationException extends RuntimeException {

    public DatabaseConstraintViolationException(String constraintName) {
        super("Database constraint violated: " + constraintName);
    }

    public DatabaseConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}

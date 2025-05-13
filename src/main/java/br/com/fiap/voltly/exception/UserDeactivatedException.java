package br.com.fiap.voltly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserDeactivatedException extends RuntimeException {
    public UserDeactivatedException(Long id) {
        super("Cannot perform operation on deactivated user with id: " + id);
    }
    
    public UserDeactivatedException(String email) {
        super("Cannot perform operation on deactivated user with email: " + email);
    }
} 
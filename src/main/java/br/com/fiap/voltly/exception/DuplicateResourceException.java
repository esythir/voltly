package br.com.fiap.voltly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String entity, String field, Object value) {
        super(String.format("%s already exists with %s: %s", entity, field, value));
    }
}
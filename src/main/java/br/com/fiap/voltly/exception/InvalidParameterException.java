package br.com.fiap.voltly.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(String parameter, String reason) {
        super("Invalid parameter '" + parameter + "': " + reason);
    }
}

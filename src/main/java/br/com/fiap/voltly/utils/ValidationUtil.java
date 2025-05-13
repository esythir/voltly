package br.com.fiap.voltly.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public final class ValidationUtil {
    private ValidationUtil() {}

    public static Map<String,String> toErrorMap(BindingResult result){
        Map<String,String> errors = new HashMap<>();
        for (FieldError fe : result.getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return errors;
    }
}

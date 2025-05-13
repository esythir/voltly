package br.com.fiap.voltly.utils;

import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.exception.InvalidUserDataException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private ValidationUtil() {}

    // Regular expression for validating email
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    
    // Password requirement: minimum 8 chars, at least one letter and one number
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    
    public static Map<String,String> toErrorMap(BindingResult result){
        Map<String,String> errors = new HashMap<>();
        for (FieldError fe : result.getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return errors;
    }
    
    /**
     * Validates all user fields and throws InvalidUserDataException if any validation fails
     */
    public static void validateUser(User user) {
        validateName(user.getName());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
        validateBirthDate(user.getBirthDate());
    }
    
    /**
     * Validates user's name
     */
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserDataException("name", "Name cannot be empty");
        }
        
        if (name.length() < 2) {
            throw new InvalidUserDataException("name", "Name must have at least 2 characters");
        }
        
        if (name.length() > 120) {
            throw new InvalidUserDataException("name", "Name cannot exceed 120 characters");
        }
    }
    
    /**
     * Validates user's email format
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserDataException("email", "Email cannot be empty");
        }
        
        if (email.length() > 180) {
            throw new InvalidUserDataException("email", "Email cannot exceed 180 characters");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidUserDataException("email", "Invalid email format");
        }
    }
    
    /**
     * Validates user's password strength
     */
    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidUserDataException("password", "Password cannot be empty");
        }
        
        if (password.length() < 8) {
            throw new InvalidUserDataException("password", "Password must have at least 8 characters");
        }
        
        if (password.length() > 100) {
            throw new InvalidUserDataException("password", "Password cannot exceed 100 characters");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidUserDataException(
                "password", 
                "Password must contain at least one letter and one number"
            );
        }
    }
    
    /**
     * Validates user's birth date
     */
    public static void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new InvalidUserDataException("birthDate", "Birth date cannot be null");
        }
        
        if (birthDate.isAfter(LocalDate.now())) {
            throw new InvalidUserDataException("birthDate", "Birth date cannot be in the future");
        }
        
        LocalDate minAge = LocalDate.now().minusYears(13);
        if (birthDate.isAfter(minAge)) {
            throw new InvalidUserDataException("birthDate", "User must be at least 13 years old");
        }
    }
    
    /**
     * Validates date range for queries
     */
    public static void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null) {
            throw new InvalidUserDataException("startDate", "Start date cannot be null");
        }
        
        if (end == null) {
            throw new InvalidUserDataException("endDate", "End date cannot be null");
        }
        
        if (start.isAfter(end)) {
            throw new InvalidUserDataException("dateRange", "Start date must be before end date");
        }
    }
    
    /**
     * Validates search terms
     */
    public static void validateSearchTerm(String term, String fieldName) {
        if (term == null || term.trim().isEmpty()) {
            throw new InvalidUserDataException(fieldName, "Search term cannot be empty");
        }
    }
}

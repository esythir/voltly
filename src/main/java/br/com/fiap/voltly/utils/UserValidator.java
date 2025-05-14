package br.com.fiap.voltly.utils;

import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.domain.repository.UserRepository;
import br.com.fiap.voltly.exception.InvalidUserDataException;
import br.com.fiap.voltly.exception.UserEmailAlreadyExistsException;
import br.com.fiap.voltly.exception.UserDeactivatedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility class for user-specific validations that require repository access
 */
public final class UserValidator {
    
    private UserValidator() {}
    
    // Advanced password pattern: at least 8 chars, with lowercase, uppercase, digit and special character
    private static final Pattern STRONG_PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    
    // Common password patterns to reject
    private static final Pattern[] COMMON_PASSWORD_PATTERNS = {
        Pattern.compile("(?i)password.*"),
        Pattern.compile("(?i)12345.*"),
        Pattern.compile("(?i)qwerty.*"),
        Pattern.compile("(?i)admin.*"),
        Pattern.compile("(?i)welcome.*"),
        Pattern.compile("(?i)letmein.*")
    };
    
    /**
     * Checks if the provided email already exists in database
     */
    public static void validateEmailUniqueness(UserRepository repository, String email) {
        repository.findByEmail(email)
            .ifPresent(existingUser -> {
                throw new UserEmailAlreadyExistsException(email);
            });
    }
    
    /**
     * Checks if the provided email already exists in database, excluding a specific user
     * (useful for update operations)
     */
    public static void validateEmailUniquenessExcept(UserRepository repository, String email, Long exceptUserId) {
        Optional<User> existingUser = repository.findByEmail(email);
        
        if (existingUser.isPresent() && !existingUser.get().getId().equals(exceptUserId)) {
            throw new UserEmailAlreadyExistsException(email);
        }
    }
    
    /**
     * Validates if user is active or throws exception otherwise
     */
    public static void validateUserActive(User user) {
        if (!user.getIsActive()) {
            if (user.getId() != null) {
                throw new UserDeactivatedException(user.getId());
            } else {
                throw new UserDeactivatedException(user.getEmail());
            }
        }
    }
    
    /**
     * Validates if a password meets strong password requirements
     */
    public static void validateStrongPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidUserDataException("password", "Password cannot be empty");
        }
        
        if (password.length() < 8) {
            throw new InvalidUserDataException("password", "Password must have at least 8 characters");
        }
        
        if (password.length() > 100) {
            throw new InvalidUserDataException("password", "Password cannot exceed 100 characters");
        }
        
        if (!STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidUserDataException(
                "password", 
                "Password must contain at least one lowercase letter, one uppercase letter, " +
                "one digit, and one special character (@$!%*?&)"
            );
        }
        
        // Check for common patterns
        for (Pattern pattern : COMMON_PASSWORD_PATTERNS) {
            if (pattern.matcher(password).matches()) {
                throw new InvalidUserDataException("password", "Password is too common or easily guessable");
            }
        }
    }
    
    /**
     * Checks if the provided password matches the stored password
     */
    public static boolean verifyPassword(String rawPassword, String encodedPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * Encodes a raw password for secure storage
     */
    public static String encodePassword(String rawPassword, PasswordEncoder encoder) {
        return encoder.encode(rawPassword);
    }
    
    /**
     * Performs complete validation for new user including strong password check
     */
    public static void validateNewUserWithStrongPassword(UserRepository repository, User user) {
        // Validate name, email and birth date
        ValidationUtil.validateName(user.getName());
        ValidationUtil.validateEmail(user.getEmail());
        ValidationUtil.validateBirthDate(user.getBirthDate());
        
        // Validate strong password
        validateStrongPassword(user.getPassword());
        
        // Validate email uniqueness
        validateEmailUniqueness(repository, user.getEmail());
    }
    
    /**
     * Performs complete validation for new user
     */
    public static void validateNewUser(UserRepository repository, User user) {
        // Validate basic user data
        ValidationUtil.validateUser(user);
        
        // Validate email uniqueness
        validateEmailUniqueness(repository, user.getEmail());
    }
    
    /**
     * Performs complete validation for user updates
     */
    public static void validateUserUpdate(UserRepository repository, User user, Long userId) {
        // Validate basic user data
        ValidationUtil.validateUser(user);
        
        // Validate email uniqueness (excluding the current user)
        validateEmailUniquenessExcept(repository, user.getEmail(), userId);
    }
    
    /**
     * Performs complete validation for user updates including strong password validation
     */
    public static void validateUserUpdateWithStrongPassword(UserRepository repository, User user, Long userId) {
        // Validate name, email and birth date
        ValidationUtil.validateName(user.getName());
        ValidationUtil.validateEmail(user.getEmail());
        ValidationUtil.validateBirthDate(user.getBirthDate());
        
        // Validate strong password
        validateStrongPassword(user.getPassword());
        
        // Validate email uniqueness (excluding the current user)
        validateEmailUniquenessExcept(repository, user.getEmail(), userId);
    }
} 
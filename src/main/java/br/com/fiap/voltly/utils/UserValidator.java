package br.com.fiap.voltly.utils;

import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.domain.repository.UserRepository;
import br.com.fiap.voltly.exception.UserEmailAlreadyExistsException;
import br.com.fiap.voltly.exception.UserDeactivatedException;
import java.util.Optional;

/**
 * Utility class for user-specific validations that require repository access
 */
public final class UserValidator {
    
    private UserValidator() {}
    
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
} 
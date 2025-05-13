package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.domain.dto.UserDto;
import br.com.fiap.voltly.domain.repository.UserRepository;
import br.com.fiap.voltly.exception.InvalidUserDataException;
import br.com.fiap.voltly.exception.UserDeactivatedException;
import br.com.fiap.voltly.exception.UserEmailAlreadyExistsException;
import br.com.fiap.voltly.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User save(User user) {
        // Check if email already exists
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new UserEmailAlreadyExistsException(user.getEmail());
                });
                
        // Validate user data
        validateUserData(user);
        
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
                
        if (!user.getIsActive()) {
            throw new UserDeactivatedException(id);
        }
        
        return user;
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
                
        if (!user.getIsActive()) {
            throw new UserDeactivatedException(email);
        }
        
        return user;
    }

    public List<User> findByBirthDateBetween(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new InvalidUserDataException("birthDate", "Start date must be before end date");
        }
        return userRepository.findByBirthDateBetween(start, end);
    }

    public List<User> searchByName(String namePart) {
        if (namePart == null || namePart.trim().isEmpty()) {
            throw new InvalidUserDataException("name", "Search term cannot be empty");
        }
        return userRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Transactional
    public User update(Long id, User updated) {
        User existing = findById(id);
        
        // Check if email is being changed and if it's already taken
        if (!existing.getEmail().equals(updated.getEmail())) {
            userRepository.findByEmail(updated.getEmail())
                    .ifPresent(existingUser -> {
                        throw new UserEmailAlreadyExistsException(updated.getEmail());
                    });
        }
        
        // Validate updated user data
        validateUserData(updated);
        
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPassword(updated.getPassword());
        existing.setBirthDate(updated.getBirthDate());
        return userRepository.save(existing);
    }

    @Transactional
    public void activate(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        u.setIsActive(true);
    }

    @Transactional
    public void deactivate(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        u.setIsActive(false);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(findById(id));
    }
    
    private void validateUserData(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new InvalidUserDataException("name", "Name cannot be empty");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new InvalidUserDataException("email", "Email cannot be empty");
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new InvalidUserDataException("password", "Password cannot be empty");
        }
        
        if (user.getBirthDate() == null) {
            throw new InvalidUserDataException("birthDate", "Birth date cannot be null");
        }
        
        if (user.getBirthDate().isAfter(LocalDate.now())) {
            throw new InvalidUserDataException("birthDate", "Birth date cannot be in the future");
        }
    }
}

package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.domain.dto.UserDto;
import br.com.fiap.voltly.domain.repository.UserRepository;
import br.com.fiap.voltly.exception.InvalidUserDataException;
import br.com.fiap.voltly.exception.UserDeactivatedException;
import br.com.fiap.voltly.exception.UserEmailAlreadyExistsException;
import br.com.fiap.voltly.exception.UserNotFoundException;
import br.com.fiap.voltly.utils.ValidationUtil;
import br.com.fiap.voltly.utils.UserValidator;
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
        // Validate the new user
        UserValidator.validateNewUser(userRepository, user);
        
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
                
        // Validate user is active
        UserValidator.validateUserActive(user);
        
        return user;
    }

    public User findByEmail(String email) {
        // Validate email format
        ValidationUtil.validateEmail(email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
                
        // Validate user is active
        UserValidator.validateUserActive(user);
        
        return user;
    }

    public List<User> findByBirthDateBetween(LocalDate start, LocalDate end) {
        // Validate date range
        ValidationUtil.validateDateRange(start, end);
        
        return userRepository.findByBirthDateBetween(start, end);
    }

    public List<User> searchByName(String namePart) {
        // Validate search term
        ValidationUtil.validateSearchTerm(namePart, "name");
        
        return userRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Transactional
    public User update(Long id, User updated) {
        // Verify the user exists
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        // Validate user is active
        UserValidator.validateUserActive(existing);
        
        // Validate the updated user data (including email uniqueness)
        UserValidator.validateUserUpdate(userRepository, updated, id);
        
        // Update the fields
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }
}

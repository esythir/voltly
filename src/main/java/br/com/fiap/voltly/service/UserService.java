package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.domain.dto.UserDto;
import br.com.fiap.voltly.domain.repository.UserRepository;
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
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("email", email);
        return user;
    }

    public List<User> findByBirthDateBetween(LocalDate start, LocalDate end) {
        return userRepository.findByBirthDateBetween(start, end);
    }

    public List<User> searchByName(String namePart) {
        return userRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Transactional
    public User update(Long id, User updated) {
        User existing = findById(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPassword(updated.getPassword());
        existing.setBirthDate(updated.getBirthDate());
        return userRepository.save(existing);
    }

    @Transactional
    public void activate(Long id) {
        User u = findById(id);
        u.setIsActive(true);
    }

    @Transactional
    public void deactivate(Long id) {
        User u = findById(id);
        u.setIsActive(false);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

}

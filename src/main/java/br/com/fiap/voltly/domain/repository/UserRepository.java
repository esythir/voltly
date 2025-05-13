package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

    public List<User> findByBirthDateBetween(LocalDate birthDateAfter, LocalDate birthDateBefore);

    @Query("""
        SELECT u\s
          FROM User u\s
         WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :namePart, '%'))
   \s""")
    List<User> findByNameContainingIgnoreCase(@Param("namePart") String namePart);
}

package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.domain.model.UserRole;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterDto(
        @NotBlank String name,
        @Email @NotBlank String email,
        @Size(min = 6, max = 20) String password,
        @NotNull LocalDate birthDate,
        UserRole role
) {
    public User toModel(String hashedPassword) {
        return User.builder()
                .name(name)
                .email(email)
                .password(hashedPassword)
                .birthDate(birthDate)
                .role(role != null ? role : UserRole.USER)
                .build();
    }
}

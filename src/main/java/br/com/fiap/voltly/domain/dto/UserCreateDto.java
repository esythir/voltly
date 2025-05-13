package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record UserCreateDto(
        @NotBlank(message = "Name is required")
        @Length(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Password is required")
        @Length(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
        String password,

        @NotNull(message = "Birth date is required")
        LocalDate birthDate
) {
    public User toModel() {
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .birthDate(birthDate)
                .build();
    }
}

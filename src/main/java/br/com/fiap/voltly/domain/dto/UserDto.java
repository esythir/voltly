package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String name,
        String email,
        LocalDate birthDate,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirthDate(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}

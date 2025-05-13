package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.User;

import java.time.LocalDate;

public record UserSummaryDto(
        String name,
        String email,
        LocalDate birthDate
) {
    public static UserSummaryDto from(User user) {
        return new UserSummaryDto(
                user.getName(),
                user.getEmail(),
                user.getBirthDate()
        );
    }
}

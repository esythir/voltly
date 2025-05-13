package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.domain.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EquipmentCreateDto(
        @NotNull(message = "Owner user id is required")
        Long userId,
        @NotBlank(message = "Name is required")
        String name,
        String description,
        @NotNull(message = "Daily limit is required")
        Double dailyLimitKwh
) {
    public Equipment toModel() {
        return Equipment.builder()
                .owner(User.builder().id(userId).build())
                .name(name)
                .description(description)
                .dailyLimitKwh(dailyLimitKwh)
                .build();
    }
}

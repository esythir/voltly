// AutomaticActionCreateDto.java
package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.AutomaticAction;
import br.com.fiap.voltly.domain.model.Equipment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AutomaticActionCreateDto(
        @NotNull(message = "Equipment ID is required")
        Long equipmentId,
        @NotBlank(message = "Type is required")
        String type,
        @NotBlank(message = "Details are required")
        String details
) {
    public AutomaticAction toModel() {
        return AutomaticAction.builder()
                .equipment(Equipment.builder().id(equipmentId).build())
                .type(type)
                .details(details)
                .executedAt(LocalDateTime.now())
                .build();
    }
}

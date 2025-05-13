// ConsumptionLimitCreateDto.java
package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.ConsumptionLimit;
import br.com.fiap.voltly.domain.model.Equipment;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConsumptionLimitCreateDto(
        @NotNull(message = "Equipment ID is required")
        Long equipmentId,
        @NotNull(message = "Limit in kWh is required")
        Double limitKwh
) {
    public ConsumptionLimit toModel() {
        return ConsumptionLimit.builder()
                .equipment(Equipment.builder().id(equipmentId).build())
                .limitKwh(limitKwh)
                .computedAt(LocalDate.now())
                .build();
    }
}

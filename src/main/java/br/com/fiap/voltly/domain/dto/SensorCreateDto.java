package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.domain.model.Sensor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorCreateDto(
        @NotBlank(message = "Serial number is required")
        String serialNumber,
        @NotBlank(message = "Type is required")
        String type,
        @NotNull(message = "Equipment ID is required")
        Long equipmentId
) {

    public Sensor toModel() {
        return Sensor.builder()
                .serialNumber(serialNumber)
                .type(type)
                .equipment(Equipment.builder().id(equipmentId).build())
                .build();
    }
}

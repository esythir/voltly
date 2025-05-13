// src/main/java/br/com/fiap/voltly/domain/dto/EnergyReadingCreateDto.java
package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.EnergyReading;
import br.com.fiap.voltly.domain.model.Sensor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EnergyReadingCreateDto(
        @NotNull(message = "Sensor ID is required")
        Long sensorId,
        @NotNull(message = "Power in kW is required")
        Double powerKw,
        @NotNull(message = "Occupancy percentage is required")
        Double occupancyPct
) {
    public EnergyReading toModel() {
        return EnergyReading.builder()
                .sensor(Sensor.builder().id(sensorId).build())
                .powerKw(powerKw)
                .occupancyPct(occupancyPct)
                .takenAt(LocalDateTime.now())
                .build();
    }
}

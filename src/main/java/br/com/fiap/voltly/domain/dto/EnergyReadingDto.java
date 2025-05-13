package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.EnergyReading;

public record EnergyReadingDto(
        Long id,
        Long sensorId,
        Double powerKw,
        Double occupancyPct,
        java.time.LocalDateTime takenAt
) {
    public static EnergyReadingDto from(EnergyReading r) {
        return new EnergyReadingDto(
                r.getId(), r.getSensor().getId(),
                r.getPowerKw(), r.getOccupancyPct(),
                r.getTakenAt()
        );
    }
}

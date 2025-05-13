package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.Sensor;

public record SensorDto(
        Long id,
        String serialNumber,
        String type,
        Long equipmentId
) {
    public static SensorDto from(Sensor s) {
        return new SensorDto(
                s.getId(),
                s.getSerialNumber(),
                s.getType(),
                s.getEquipment().getId()
        );
    }
}
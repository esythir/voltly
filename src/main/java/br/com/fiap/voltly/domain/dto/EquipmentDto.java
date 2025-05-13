package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.Equipment;

public record EquipmentDto(
        Long id,
        Long ownerId,
        String name,
        String description,
        Double dailyLimitKwh,
        Boolean active
) {
    public static EquipmentDto from(Equipment e) {
        return new EquipmentDto(
                e.getId(),
                e.getOwner().getId(),
                e.getName(),
                e.getDescription(),
                e.getDailyLimitKwh(),
                e.getActive()
        );
    }
}

package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.ConsumptionLimit;

public record ConsumptionLimitDto(
        Long id,
        Long equipmentId,
        Double limitKwh,
        java.time.LocalDate computedAt
) {
    public static ConsumptionLimitDto from(ConsumptionLimit c) {
        return new ConsumptionLimitDto(
                c.getId(), c.getEquipment().getId(),
                c.getLimitKwh(), c.getComputedAt()
        );
    }
}

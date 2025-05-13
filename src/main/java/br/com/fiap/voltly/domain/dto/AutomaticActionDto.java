package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.AutomaticAction;

public record AutomaticActionDto(
        Long id,
        Long equipmentId,
        String type,
        String details,
        java.time.LocalDateTime executedAt
) {
    public static AutomaticActionDto from(AutomaticAction a) {
        return new AutomaticActionDto(
                a.getId(), a.getEquipment().getId(),
                a.getType(), a.getDetails(), a.getExecutedAt()
        );
    }
}

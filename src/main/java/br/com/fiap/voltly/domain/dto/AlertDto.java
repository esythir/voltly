package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.Alert;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AlertDto(
        Long id,
        Long equipmentId,
        String equipmentName,
        LocalDate alertDate,
        Double consumptionKwh,
        Double limitKwh,
        Double exceededByKwh,
        String message,
        LocalDateTime createdAt
) {
    public static AlertDto from(Alert a) {
        return new AlertDto(
                a.getId(),
                a.getEquipment().getId(),
                a.getEquipment().getName(),
                a.getAlertDate(),
                a.getConsumptionKwh(),
                a.getLimitKwh(),
                a.getExceededByKwh(),
                a.getMessage(),
                a.getCreatedAt()
        );
    }
}

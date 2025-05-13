package br.com.fiap.voltly.domain.dto;

import br.com.fiap.voltly.domain.model.DailyReport;
import br.com.fiap.voltly.enums.EfficiencyRating;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DailyReportDto(
        Long id,
        Long equipmentId,
        String equipmentName,
        LocalDate reportDate,
        Double consumptionKwh,
        Double co2EmissionKg,
        EfficiencyRating efficiencyRating,
        LocalDateTime createdAt
) {
    public static DailyReportDto from(DailyReport r) {
        return new DailyReportDto(
                r.getId(),
                r.getEquipment().getId(),
                r.getEquipment().getName(),
                r.getReportDate(),
                r.getConsumptionKwh(),
                r.getCo2EmissionKg(),
                r.getEfficiencyRating(),
                r.getCreatedAt()
        );
    }
}

package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.dto.Co2ReportDto;
import br.com.fiap.voltly.domain.model.DailyReport;
import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.domain.repository.DailyReportRepository;
import br.com.fiap.voltly.domain.repository.EnergyReadingRepository;
import br.com.fiap.voltly.domain.repository.EquipmentRepository;
import br.com.fiap.voltly.enums.EfficiencyRating;
import br.com.fiap.voltly.utils.CarbonCalculatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private final EquipmentRepository equipmentRepo;
    private final EnergyReadingRepository readingRepo;
    private final DailyReportRepository reportRepo;

    @Transactional
    public List<DailyReport> generateDailyConsumptionReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end   = date.plusDays(1).atStartOfDay();

        List<DailyReport> out = new ArrayList<>();
        for (Equipment eq : equipmentRepo.findAll()) {
            double consumption = readingRepo
                    .sumPowerKwByEquipmentAndTakenAtBetween(eq.getId(), start, end);
            double co2  = CarbonCalculatorUtil.calculateEmission(consumption);
            var rating  = EfficiencyRating.classify(consumption, eq.getDailyLimitKwh());

            DailyReport rpt = DailyReport.builder()
                    .equipment(eq)
                    .reportDate(date)
                    .consumptionKwh(consumption)
                    .co2EmissionKg(co2)
                    .efficiencyRating(rating)
                    .build();
            reportRepo.save(rpt);
            out.add(rpt);
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Co2ReportDto> getDailyCo2(LocalDate date, Long equipmentId) {
        List<DailyReport> reports = (equipmentId == null)
                ? reportRepo.findAllByReportDate(date)
                : reportRepo.findAllByReportDateAndEquipmentId(date, equipmentId);

        return reports.stream()
                .map(r -> new Co2ReportDto(
                        r.getEquipment().getId(),
                        r.getCo2EmissionKg()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DailyReport> getReportsByDateAndEquipment(LocalDate date, Long equipmentId) {
        return (equipmentId != null)
                ? reportRepo.findAllByReportDateAndEquipmentId(date, equipmentId)
                : reportRepo.findAllByReportDate(date);
    }
}

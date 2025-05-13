package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.Alert;
import br.com.fiap.voltly.domain.model.ConsumptionLimit;
import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.domain.repository.AlertRepository;
import br.com.fiap.voltly.domain.repository.ConsumptionLimitRepository;
import br.com.fiap.voltly.domain.repository.EnergyReadingRepository;
import br.com.fiap.voltly.domain.repository.EquipmentRepository;
import br.com.fiap.voltly.exception.ResourceNotFoundException;
import br.com.fiap.voltly.utils.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final EquipmentRepository        equipmentRepo;
    private final EnergyReadingRepository    readingRepo;
    private final ConsumptionLimitRepository limitRepo;
    private final AlertRepository            alertRepo;
    private final NotificationService        notifier;

    @Transactional
    public List<Alert> generateAlerts(LocalDate date, Long equipmentId) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        LocalDateTime start = d.atStartOfDay();
        LocalDateTime end   = d.plusDays(1).atStartOfDay();

        List<Equipment> eqs = (equipmentId != null)
                ? List.of(equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", equipmentId)))
                : equipmentRepo.findAll();

        List<Alert> out = new ArrayList<>();
        for (Equipment eq : eqs) {
            double consumption = readingRepo
                    .sumPowerKwByEquipmentAndTakenAtBetween(eq.getId(), start, end);
            ConsumptionLimit limit = limitRepo.findByEquipmentId(eq.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("ConsumptionLimit", eq.getId()));

            if (consumption > limit.getLimitKwh()) {
                double exceeded = consumption - limit.getLimitKwh();
                String msg = String.format(
                        "Consumed %.2f kWh, exceeds limit %.2f kWh by %.2f kWh",
                        consumption, limit.getLimitKwh(), exceeded
                );

                Alert alert = Alert.builder()
                        .equipment(eq)
                        .alertDate(d)
                        .consumptionKwh(consumption)
                        .limitKwh(limit.getLimitKwh())
                        .exceededByKwh(exceeded)
                        .message(msg)
                        .build();

                alertRepo.save(alert);
                notifier.notifyAll(alert);
                out.add(alert);
            }
        }
        return out;
    }

    @Transactional(readOnly = true)
    public List<Alert> getAlerts(LocalDate date, Long equipmentId) {
        if (date != null && equipmentId != null) {
            return alertRepo.findAllByAlertDateAndEquipmentId(date, equipmentId);
        } else if (date != null) {
            return alertRepo.findAllByAlertDate(date);
        } else {
            return alertRepo.findAll();
        }
    }
}

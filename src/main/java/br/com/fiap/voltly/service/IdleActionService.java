package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.AutomaticAction;
import br.com.fiap.voltly.domain.model.Sensor;
import br.com.fiap.voltly.domain.repository.AutomaticActionRepository;
import br.com.fiap.voltly.domain.repository.EnergyReadingRepository;
import br.com.fiap.voltly.domain.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdleActionService {

    private final SensorRepository          sensorRepository;
    private final EnergyReadingRepository   readingRepository;
    private final AutomaticActionRepository actionRepository;

    @Transactional
    public List<AutomaticAction> detectIdleActions(
            double thresholdKw,
            double occupancyPercentage,
            int windowMinutes,
            Long equipmentId
    ) {
        List<Sensor> sensors = (equipmentId != null)
                ? sensorRepository.findByEquipmentId(equipmentId)
                : sensorRepository.findAll();

        LocalDateTime since = LocalDateTime.now().minusMinutes(windowMinutes);
        var page3 = PageRequest.of(0, 3);

        List<AutomaticAction> result = new ArrayList<>();
        for (Sensor sensor : sensors) {
            var readings = readingRepository
                    .findBySensorIdAndTakenAtAfterOrderByTakenAtDesc(
                            sensor.getId(), since, page3);

            boolean allIdle =
                    readings.size() == 3 &&
                            readings.stream().allMatch(r ->
                                    r.getPowerKw() < thresholdKw &&
                                            r.getOccupancyPct() <= occupancyPercentage
                            );

            if (allIdle) {
                var action = AutomaticAction.builder()
                        .equipment(sensor.getEquipment())
                        .type("SHUTDOWN")
                        .details(String.format(
                                "Idle detected on sensor %d: last 3 readings under %.2f kW and occupancy ≤ %.2f%%",
                                sensor.getId(), thresholdKw, occupancyPercentage))
                        .executedAt(LocalDateTime.now())
                        .build();
                actionRepository.save(action);
                result.add(action);
            }
        }

        return result;
    }
}

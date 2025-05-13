package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.ConsumptionLimit;
import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.domain.repository.ConsumptionLimitRepository;
import br.com.fiap.voltly.domain.repository.EnergyReadingRepository;
import br.com.fiap.voltly.domain.repository.EquipmentRepository;
import br.com.fiap.voltly.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumptionLimitService {

    private final ConsumptionLimitRepository repository;
    private final EquipmentRepository        equipmentRepo;
    private final EnergyReadingRepository    readingRepo;


    public Page<ConsumptionLimit> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public ConsumptionLimit findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ConsumptionLimit", id));
    }

    public ConsumptionLimit findByEquipment(Long equipmentId) {
        return repository.findByEquipmentId(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("ConsumptionLimit", equipmentId));
    }

    public List<ConsumptionLimit> findAllByOwner(Long ownerId) {
        return repository.findAllByEquipmentOwnerId(ownerId);
    }

    @Transactional
    public ConsumptionLimit save(ConsumptionLimit limit) {
        return repository.save(limit);
    }

    @Transactional
    public ConsumptionLimit update(Long id, ConsumptionLimit updated) {
        ConsumptionLimit existing = findById(id);
        existing.setLimitKwh(updated.getLimitKwh());
        existing.setComputedAt(updated.getComputedAt());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    @Transactional
    public List<ConsumptionLimit> recalculateMonthlyLimits(YearMonth yearMonth) {
        YearMonth ym = (yearMonth != null) ? yearMonth : YearMonth.now().minusMonths(1);

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.plusMonths(1).atDay(1).atStartOfDay();
        int daysInMonth     = ym.lengthOfMonth();
        LocalDate computedAt = LocalDate.now();

        List<ConsumptionLimit> result = new ArrayList<>();

        for (Equipment eq : equipmentRepo.findAll()) {
            double totalKwh = readingRepo
                    .sumPowerKwByEquipmentAndTakenAtBetween(eq.getId(), start, end);

            double dailyAvg = totalKwh / daysInMonth;
            double newLimit = dailyAvg * 1.10;

            // upsert
            ConsumptionLimit limit = repository
                    .findByEquipmentId(eq.getId())
                    .map(existing -> {
                        existing.setLimitKwh(newLimit);
                        existing.setComputedAt(computedAt);
                        return existing;
                    })
                    .orElseGet(() ->
                            ConsumptionLimit.builder()
                                    .equipment(eq)
                                    .limitKwh(newLimit)
                                    .computedAt(computedAt)
                                    .build()
                    );

            repository.save(limit);
            result.add(limit);
        }

        return result;
    }
}

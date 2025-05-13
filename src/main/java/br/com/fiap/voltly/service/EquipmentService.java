package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.domain.repository.*;
import br.com.fiap.voltly.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository        equipmentRepo;
    private final SensorRepository           sensorRepo;
    private final EnergyReadingRepository    readingRepo;
    private final ConsumptionLimitRepository limitRepo;
    private final AutomaticActionRepository  actionRepo;
    private final DailyReportRepository      dailyReportRepo;

    @Transactional
    public Equipment save(Equipment equipment) {
        return equipmentRepo.save(equipment);
    }

    public Equipment findById(Long id) {
        return equipmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", id));
    }

    public Page<Equipment> findAll(Pageable pageable) {
        return equipmentRepo.findAll(pageable);
    }

    public Equipment findByName(String name) {
        return equipmentRepo.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", name));
    }

    @Transactional
    public Equipment update(Long id, Equipment updated) {
        Equipment ex = findById(id);
        ex.setName(updated.getName());
        ex.setDescription(updated.getDescription());
        ex.setDailyLimitKwh(updated.getDailyLimitKwh());
        return equipmentRepo.save(ex);
    }

    @Transactional public void activate(Long id)    { findById(id).setActive(true);  }

    @Transactional public void deactivate(Long id)  { findById(id).setActive(false); }

    @Transactional
    public void delete(Long id) {
        Equipment eq = findById(id);

        dailyReportRepo.deleteAllByEquipmentId(id);

        eq.getSensors().forEach(s -> readingRepo.deleteBySensorId(s.getId()));
        sensorRepo.deleteAll(eq.getSensors());
        limitRepo.deleteByEquipmentId(id);
        actionRepo.deleteByEquipmentId(id);
        equipmentRepo.delete(eq);
    }

    public List<Equipment> findByOwner(Long ownerId) {
        return equipmentRepo.findByOwnerId(ownerId);
    }

    public boolean isOwner(Long equipmentId, User principal) {
        if (principal == null) return false;
        return equipmentRepo.findByIdAndOwnerId(equipmentId, principal.getId()).isPresent();
    }
}

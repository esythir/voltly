package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.ConsumptionLimit;
import br.com.fiap.voltly.domain.repository.ConsumptionLimitRepository;
import br.com.fiap.voltly.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumptionLimitService {

    private final ConsumptionLimitRepository repository;

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
}

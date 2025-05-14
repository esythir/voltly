package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.EnergyReading;
import br.com.fiap.voltly.domain.repository.EnergyReadingRepository;
import br.com.fiap.voltly.exception.InvalidParameterException;
import br.com.fiap.voltly.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnergyReadingService {

    private final EnergyReadingRepository repository;

    @Transactional
    public EnergyReading save(EnergyReading reading) {
        if (reading.getTakenAt().isAfter(java.time.LocalDateTime.now())) {
            throw new InvalidParameterException("takenAt", "cannot be in the future");
        }
        return repository.save(reading);
    }

    public List<EnergyReading> findAllForOwner(Long ownerId) {
        return repository.findAllForOwner(ownerId);
    }

    public EnergyReading findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EnergyReading", id));
    }

    public Page<EnergyReading> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<EnergyReading> lastThreeForSensor(Long sensorId) {
        return repository.findTop3BySensorIdOrderByTakenAtDesc(sensorId);
    }

    public List<EnergyReading> forSensorInWindow(Long sensorId,
                                                 LocalDateTime start,
                                                 LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new InvalidParameterException("start/end", "start must be before end");
        }
        return repository.findBySensorIdAndTakenAtBetween(sensorId, start, end);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

}

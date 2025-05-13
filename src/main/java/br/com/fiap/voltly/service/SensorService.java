package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.dto.SensorUpdateDto;
import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.domain.model.Sensor;
import br.com.fiap.voltly.domain.repository.SensorRepository;
import br.com.fiap.voltly.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorService {

    private final SensorRepository repository;

    @Transactional
    public Sensor save(Sensor sensor) {
        return repository.save(sensor);
    }

    public Sensor findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", id));
    }

    public Page<Sensor> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Sensor findBySerial(String serial) {
        return repository.findBySerialNumber(serial)
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", serial));
    }

    @Transactional
    public Sensor update(Long id, SensorUpdateDto dto) {
        Sensor existing = findById(id);
        existing.setSerialNumber(dto.serialNumber());
        existing.setType(dto.type());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }
}

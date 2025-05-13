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

import java.util.List;

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

    public List<Sensor> findByOwner(Long ownerId) {
        return repository.findByEquipmentOwnerId(ownerId);
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

    public boolean isOwner(Long sensorId, br.com.fiap.voltly.domain.model.User principal) {
        return repository.findById(sensorId)
                .map(s -> s.getEquipment().getOwner().getId().equals(principal.getId()))
                .orElse(false);
    }

    public boolean isOwnerBySerial(String serial, br.com.fiap.voltly.domain.model.User principal) {
        return repository.findBySerialNumber(serial)
                .map(s -> s.getEquipment().getOwner().getId().equals(principal.getId()))
                .orElse(false);
    }

}

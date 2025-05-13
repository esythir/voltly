package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findBySerialNumber(String serialNumber);

    List<Sensor> findByEquipmentId(Long equipmentId);

    List<Sensor> findByEquipmentOwnerId(Long ownerId);

}

package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.EnergyReading;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EnergyReadingRepository extends JpaRepository<EnergyReading, Long> {

    List<EnergyReading> findTop3BySensorIdOrderByTakenAtDesc(Long sensorId);

    List<EnergyReading> findBySensorIdAndTakenAtBetween(
            Long sensorId,
            LocalDateTime start,
            LocalDateTime end);

    List<EnergyReading> findBySensorIdAndTakenAtAfterOrderByTakenAtDesc(
            Long sensorId,
            LocalDateTime since,
            Pageable pageable
    );

    void deleteBySensorId(Long sensorId);
}

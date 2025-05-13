package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.EnergyReading;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
      SELECT COALESCE(SUM(r.powerKw), 0) 
        FROM EnergyReading r 
       WHERE r.sensor.equipment.id = :equipmentId 
         AND r.takenAt >= :start 
         AND r.takenAt < :end
    """)
    Double sumPowerKwByEquipmentAndTakenAtBetween(
            @Param("equipmentId") Long equipmentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    void deleteBySensorId(Long sensorId);
}

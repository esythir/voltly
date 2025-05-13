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
      SELECT COALESCE(SUM(r.powerKw), 0)\s
        FROM EnergyReading r\s
       WHERE r.sensor.equipment.id = :equipmentId\s
         AND r.takenAt >= :start\s
         AND r.takenAt < :end
   \s""")
    Double sumPowerKwByEquipmentAndTakenAtBetween(
            @Param("equipmentId") Long equipmentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("""
     SELECT r
       FROM EnergyReading r
      WHERE r.sensor.equipment.owner.id = :ownerId
    """)
    List<EnergyReading> findAllForOwner(@Param("ownerId") Long ownerId);


    void deleteBySensorId(Long sensorId);
}

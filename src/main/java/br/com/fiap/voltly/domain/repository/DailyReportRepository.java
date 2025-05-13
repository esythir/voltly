package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> findAllByReportDate(LocalDate reportDate);

    List<DailyReport> findAllByReportDateAndEquipmentId(LocalDate reportDate, Long equipmentId);

    @Modifying
    @Query("DELETE FROM DailyReport d WHERE d.equipment.id = :equipmentId")
    void deleteAllByEquipmentId(@Param("equipmentId") Long equipmentId);

}

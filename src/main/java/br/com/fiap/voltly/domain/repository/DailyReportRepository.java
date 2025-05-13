package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> findAllByReportDate(LocalDate reportDate);
    List<DailyReport> findAllByReportDateAndEquipmentId(LocalDate reportDate, Long equipmentId);

}

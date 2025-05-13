package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByAlertDate(LocalDate alertDate);
    List<Alert> findAllByAlertDateAndEquipmentId(LocalDate alertDate, Long equipmentId);
}

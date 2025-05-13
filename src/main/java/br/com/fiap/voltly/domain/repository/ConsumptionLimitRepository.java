package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.ConsumptionLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsumptionLimitRepository extends JpaRepository<ConsumptionLimit, Long> {

    Optional<ConsumptionLimit> findByEquipmentId(Long equipmentId);

    void deleteByEquipmentId(Long equipmentId);

    List<ConsumptionLimit> findAllByEquipmentOwnerId(Long ownerId);

}

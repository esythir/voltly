package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.AutomaticAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutomaticActionRepository extends JpaRepository<AutomaticAction, Long> {

    List<AutomaticAction> findByEquipmentId(Long equipmentId);

    void deleteByEquipmentId(Long equipmentId);

    List<AutomaticAction> findAllByEquipmentOwnerId(Long ownerId);

}

package br.com.fiap.voltly.domain.repository;

import br.com.fiap.voltly.domain.model.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Optional<Equipment> findByIdAndOwnerId(Long id, Long ownerId);

    Page<Equipment> findAllByOwnerId(Long ownerId, Pageable pageable);

    List<Equipment> findByOwnerId(Long ownerId);

    Optional<Equipment> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}

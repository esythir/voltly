package br.com.fiap.voltly.service;

import br.com.fiap.voltly.domain.model.AutomaticAction;
import br.com.fiap.voltly.domain.repository.AutomaticActionRepository;
import br.com.fiap.voltly.exception.InvalidParameterException;
import br.com.fiap.voltly.exception.ResourceNotFoundException;
import br.com.fiap.voltly.utils.BusinessValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutomaticActionService {

    private final AutomaticActionRepository repository;

    @Transactional
    public AutomaticAction save(AutomaticAction action) {
        BusinessValidationUtil.assertNotNull(action.getEquipment(), "equipment");
        BusinessValidationUtil.assertNotBlank(action.getType(), "type");

        if (action.getDetails() != null && action.getDetails().length() > 500) {
            throw new InvalidParameterException("details", "max length is 500 chars");
        }
        return repository.save(action);
    }

    public AutomaticAction findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutomaticAction", id));
    }

    public Page<AutomaticAction> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<AutomaticAction> findByEquipment(Long equipmentId) {
        return repository.findByEquipmentId(equipmentId);
    }

    public List<AutomaticAction> findAllByOwner(Long ownerId) {
        return repository.findAllByEquipmentOwnerId(ownerId);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }
}

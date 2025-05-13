package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.EquipmentCreateDto;
import br.com.fiap.voltly.domain.dto.EquipmentDto;
import br.com.fiap.voltly.domain.model.Equipment;
import br.com.fiap.voltly.service.EquipmentService;
import br.com.fiap.voltly.utils.DTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService service;

    @PostMapping
    public ResponseEntity<EquipmentDto> create(@Valid @RequestBody EquipmentCreateDto dto) {
        var entity = dto.toModel();
        var saved  = service.save(entity);
        return ResponseEntity
                .created(URI.create("/api/equipments/" + saved.getId()))
                .body(EquipmentDto.from(saved));
    }

    @GetMapping
    public ResponseEntity<Page<EquipmentDto>> list(Pageable pageable) {
        return ResponseEntity.ok(
                DTOMapper.mapPage(service.findAll(pageable), EquipmentDto::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(EquipmentDto.from(service.findById(id)));
    }

    @GetMapping(params = "name")
    public ResponseEntity<EquipmentDto> byName(@RequestParam String name) {
        return ResponseEntity.ok(EquipmentDto.from(service.findByName(name)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDto> update(
            @PathVariable Long id,
            @Valid @RequestBody Equipment equipment
    ) {
        return ResponseEntity.ok(EquipmentDto.from(service.update(id, equipment)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

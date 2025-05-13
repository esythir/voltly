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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService service;

    // ADMIN

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EquipmentDto>> listAllPageable(Pageable pageable) {
        return ResponseEntity.ok(
                DTOMapper.mapPage(service.findAll(pageable), EquipmentDto::from));
    }

    // SHARED / OWNER

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') || principal.id == #dto.ownerId()")
    public ResponseEntity<EquipmentDto> createEquipment(@Valid @RequestBody EquipmentCreateDto dto,
                                               @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        Equipment saved = service.save(dto.toModel());
        return ResponseEntity.created(URI.create("/api/equipments/" + saved.getId()))
                .body(EquipmentDto.from(saved));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @equipmentService.isOwner(#id, principal)")
    public ResponseEntity<EquipmentDto> getById(@PathVariable Long id,
                                            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        return ResponseEntity.ok(EquipmentDto.from(service.findById(id)));
    }

    @GetMapping(params = "name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDto> getByName(@RequestParam String name) {
        return ResponseEntity.ok(EquipmentDto.from(service.findByName(name)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @equipmentService.isOwner(#id, principal)")
    public ResponseEntity<EquipmentDto> updateById(@PathVariable Long id,
                                               @Valid @RequestBody Equipment dto,
                                               @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        return ResponseEntity.ok(EquipmentDto.from(service.update(id, dto)));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') || @equipmentService.isOwner(#id, principal)")
    public ResponseEntity<Void> activate(@PathVariable Long id,
                                         @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') || @equipmentService.isOwner(#id, principal)")
    public ResponseEntity<Void> deactivate(@PathVariable Long id,
                                           @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @equipmentService.isOwner(#id, principal)")
    public ResponseEntity<Void> deleteById(@PathVariable Long id,
                                       @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // USER

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<EquipmentDto>> myEquipments(@AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        var dtos = service.findByOwner(principal.getId())
                .stream().map(EquipmentDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

}

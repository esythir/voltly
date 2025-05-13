package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.AutomaticActionCreateDto;
import br.com.fiap.voltly.domain.dto.AutomaticActionDto;
import br.com.fiap.voltly.service.AutomaticActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class AutomaticActionController {

    private final AutomaticActionService service;

    // ADMIN

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AutomaticActionDto> createAction(@Valid @RequestBody AutomaticActionCreateDto dto) {
        var saved = service.save(dto.toModel());
        return ResponseEntity.created(URI.create("/api/actions/" + saved.getId()))
                .body(AutomaticActionDto.from(saved));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AutomaticActionDto>> listAllPageable(Pageable pageable) {
        return ResponseEntity.ok(
                service.findAll(pageable).map(AutomaticActionDto::from));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AutomaticActionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(AutomaticActionDto.from(service.findById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // SHARED / OWNER

    @GetMapping("/equipment/{equipmentId}")
    @PreAuthorize("hasRole('ADMIN') or @equipmentService.isOwner(#equipmentId, principal)")
    public ResponseEntity<List<AutomaticActionDto>> getByEquipment(
            @PathVariable Long equipmentId) {
        var list = service.findByEquipment(equipmentId);
        return ResponseEntity.ok(list.stream()
                .map(AutomaticActionDto::from).toList());
    }

    // USER

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AutomaticActionDto>> myActions(
            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        var dtos = service.findAllByOwner(principal.getId())
                .stream()
                .map(AutomaticActionDto::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}

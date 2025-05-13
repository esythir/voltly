package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.AutomaticActionCreateDto;
import br.com.fiap.voltly.domain.dto.AutomaticActionDto;
import br.com.fiap.voltly.service.AutomaticActionService;
import br.com.fiap.voltly.utils.DTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class AutomaticActionController {

    private final AutomaticActionService service;

    @PostMapping
    public ResponseEntity<AutomaticActionDto> create(@Valid @RequestBody AutomaticActionCreateDto dto) {
        var saved = service.save(dto.toModel());
        return ResponseEntity
                .created(URI.create("/api/actions/" + saved.getId()))
                .body(AutomaticActionDto.from(saved));
    }

    @GetMapping
    public ResponseEntity<Page<AutomaticActionDto>> listAll(Pageable pageable) {
        return ResponseEntity.ok(
                DTOMapper.mapPage(service.findAll(pageable), AutomaticActionDto::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutomaticActionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(AutomaticActionDto.from(service.findById(id)));
    }

    @GetMapping(params = "equipmentId")
    public ResponseEntity<List<AutomaticActionDto>> getByEquipment(@RequestParam Long equipmentId) {
        var dtos = service.findByEquipment(equipmentId)
                .stream().map(AutomaticActionDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

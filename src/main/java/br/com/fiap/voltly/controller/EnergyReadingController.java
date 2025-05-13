package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.EnergyReadingCreateDto;
import br.com.fiap.voltly.domain.dto.EnergyReadingDto;
import br.com.fiap.voltly.service.EnergyReadingService;
import br.com.fiap.voltly.utils.DTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
public class EnergyReadingController {

    private final EnergyReadingService service;

    @PostMapping
    public ResponseEntity<EnergyReadingDto> create(@Valid @RequestBody EnergyReadingCreateDto dto) {
        var saved = service.save(dto.toModel());
        return ResponseEntity
                .created(URI.create("/api/readings/" + saved.getId()))
                .body(EnergyReadingDto.from(saved));
    }

    @GetMapping
    public ResponseEntity<Page<EnergyReadingDto>> list(Pageable pageable) {
        return ResponseEntity.ok(
                DTOMapper.mapPage(service.findAll(pageable), EnergyReadingDto::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnergyReadingDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(EnergyReadingDto.from(service.findById(id)));
    }

    @GetMapping("/sensor/{sensorId}/last")
    public ResponseEntity<List<EnergyReadingDto>> getLastThree(@PathVariable Long sensorId) {
        var dtos = service.lastThreeForSensor(sensorId)
                .stream().map(EnergyReadingDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<List<EnergyReadingDto>> getLast(
            @PathVariable Long sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        var dtos = service.forSensorInWindow(sensorId, start, end)
                .stream().map(EnergyReadingDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.EnergyReadingCreateDto;
import br.com.fiap.voltly.domain.dto.EnergyReadingDto;
import br.com.fiap.voltly.domain.dto.SensorDto;
import br.com.fiap.voltly.service.EnergyReadingService;
import br.com.fiap.voltly.utils.DTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
public class EnergyReadingController {

    private final EnergyReadingService service;

    // ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnergyReadingDto> createSensor(@Valid @RequestBody EnergyReadingCreateDto dto) {
        var saved = service.save(dto.toModel());
        return ResponseEntity.created(URI.create("/api/readings/" + saved.getId()))
                .body(EnergyReadingDto.from(saved));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @readingAuth.isOwnerByReading(#id, principal)")
    public ResponseEntity<EnergyReadingDto> getById(@PathVariable Long id,
                                                @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        return ResponseEntity.ok(EnergyReadingDto.from(service.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EnergyReadingDto>> listAllPageable(Pageable pageable) {
        return ResponseEntity.ok(
                DTOMapper.mapPage(service.findAll(pageable), EnergyReadingDto::from));
    }

    @GetMapping("/sensor/{sensorId}/last")
    @PreAuthorize("hasRole('ADMIN') || @sensorService.isOwner(#sensorId, principal)")
    public ResponseEntity<List<EnergyReadingDto>> getLastThree(@PathVariable Long sensorId,
                                                            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        var list = service.lastThreeForSensor(sensorId)
                .stream().map(EnergyReadingDto::from).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/sensor/{sensorId}")
    @PreAuthorize("hasRole('ADMIN') || @sensorService.isOwner(#sensorId, principal)")
    public ResponseEntity<List<EnergyReadingDto>> getLastInWindow(
            @PathVariable Long sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {

        var list = service.forSensorInWindow(sensorId, start, end)
                .stream().map(EnergyReadingDto::from).toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // USER

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<EnergyReadingDto>> myReadings(
            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {

        var list = service.findAllForOwner(principal.getId())
                .stream().map(EnergyReadingDto::from).toList();
        return ResponseEntity.ok(list);
    }

}

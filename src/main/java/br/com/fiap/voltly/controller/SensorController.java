package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.SensorCreateDto;
import br.com.fiap.voltly.domain.dto.SensorDto;
import br.com.fiap.voltly.domain.dto.SensorUpdateDto;
import br.com.fiap.voltly.domain.model.Sensor;
import br.com.fiap.voltly.service.SensorService;
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
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService service;

    // ADMIN

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') || @equipmentService.isOwner(#dto.equipmentId(), principal)")
    public ResponseEntity<SensorDto> createSensor(@Valid @RequestBody SensorCreateDto dto,
                                            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        Sensor saved = service.save(dto.toModel());
        return ResponseEntity
                .created(URI.create("/api/sensors/" + saved.getId()))
                .body(SensorDto.from(saved));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<SensorDto>> listAllPageable(Pageable pageable) {
        return ResponseEntity.ok(
                DTOMapper.mapPage(service.findAll(pageable), SensorDto::from));
    }

    // SHARED / OWNER

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @sensorService.isOwner(#id, principal)")
    public ResponseEntity<SensorDto> getById(@PathVariable Long id,
                                         @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        return ResponseEntity.ok(SensorDto.from(service.findById(id)));
    }

    @GetMapping(params = "serial")
    @PreAuthorize("hasRole('ADMIN') || @sensorService.isOwnerBySerial(#serial, principal)")
    public ResponseEntity<SensorDto> getBySerial(@RequestParam String serial,
                                              @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        return ResponseEntity.ok(SensorDto.from(service.findBySerial(serial)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @sensorService.isOwner(#id, principal)")
    public ResponseEntity<SensorDto> updateById(@PathVariable Long id,
                                            @Valid @RequestBody SensorUpdateDto dto,
                                            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        return ResponseEntity.ok(SensorDto.from(service.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || @sensorService.isOwner(#id, principal)")
    public ResponseEntity<Void> deleteById(@PathVariable Long id,
                                       @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // USER

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<SensorDto>> mySensors(@AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        List<SensorDto> dtos = service.findByOwner(principal.getId())
                .stream().map(SensorDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

}

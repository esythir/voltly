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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService service;

    @PostMapping
    public ResponseEntity<SensorDto> create(@Valid @RequestBody SensorCreateDto dto) {
        var entity = dto.toModel();
        var saved  = service.save(entity);
        URI uri = URI.create("/api/sensors/" + saved.getId());
        return ResponseEntity
                .created(uri)
                .body(SensorDto.from(saved));
    }

    @GetMapping("/{id}")
    public SensorDto getById(@PathVariable Long id) {
        return SensorDto.from(service.findById(id));
    }

    @GetMapping(params = "serial")
    public ResponseEntity<SensorDto> getBySerial(@RequestParam String serial) {
        Sensor sensor = service.findBySerial(serial);
        return ResponseEntity.ok(SensorDto.from(sensor));
    }

    @GetMapping
    public Page<SensorDto> listAll(Pageable pageable) {
        return DTOMapper.mapPage(service.findAll(pageable), SensorDto::from);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SensorDto> update(
            @PathVariable Long id,
            @Valid @RequestBody SensorUpdateDto dto
    ) {
        var updated = service.update(id, dto);
        return ResponseEntity.ok(SensorDto.from(updated));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

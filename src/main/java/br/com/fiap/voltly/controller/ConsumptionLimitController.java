package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.ConsumptionLimitCreateDto;
import br.com.fiap.voltly.domain.dto.ConsumptionLimitDto;
import br.com.fiap.voltly.domain.model.ConsumptionLimit;
import br.com.fiap.voltly.service.ConsumptionLimitService;
import br.com.fiap.voltly.utils.DTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/limits")
@RequiredArgsConstructor
public class ConsumptionLimitController {

    private final ConsumptionLimitService service;

    @PostMapping
    public ResponseEntity<ConsumptionLimitDto> create(@Valid @RequestBody ConsumptionLimitCreateDto dto) {
        var saved = service.save(dto.toModel());
        return ResponseEntity
                .created(URI.create("/api/limits/" + saved.getId()))
                .body(ConsumptionLimitDto.from(saved));
    }

    @GetMapping
    public ResponseEntity<Page<ConsumptionLimitDto>> list(Pageable pageable) {
        return ResponseEntity.ok(
                DTOMapper.mapPage(service.findAll(pageable), ConsumptionLimitDto::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsumptionLimitDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(ConsumptionLimitDto.from(service.findById(id)));
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<ConsumptionLimitDto> byEquipment(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(ConsumptionLimitDto.from(service.findByEquipment(equipmentId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsumptionLimitDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ConsumptionLimit limit
    ) {
        return ResponseEntity.ok(ConsumptionLimitDto.from(service.update(id, limit)));
    }

    @PostMapping("/monthly-recalculation")
    public ResponseEntity<List<ConsumptionLimitDto>> recalculateMonthly(
            @RequestParam(value = "yearMonth", required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        var limits = service.recalculateMonthlyLimits(yearMonth);
        var dtos   = limits.stream()
                .map(ConsumptionLimitDto::from)
                .collect(Collectors.toList());

        YearMonth ym = (yearMonth != null ? yearMonth : YearMonth.now().minusMonths(1));
        URI uri = URI.create("/api/limits/monthly-recalculation?yearMonth=" + ym);

        return ResponseEntity.created(uri).body(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

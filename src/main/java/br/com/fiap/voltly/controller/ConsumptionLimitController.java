package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.ConsumptionLimitCreateDto;
import br.com.fiap.voltly.domain.dto.ConsumptionLimitDto;
import br.com.fiap.voltly.domain.model.ConsumptionLimit;
import br.com.fiap.voltly.service.ConsumptionLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/limits")
@RequiredArgsConstructor
public class ConsumptionLimitController {

    private final ConsumptionLimitService service;

    // ADMIN

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsumptionLimitDto> createLimit(@Valid @RequestBody ConsumptionLimitCreateDto dto) {
        var saved = service.save(dto.toModel());
        return ResponseEntity
                .created(URI.create("/api/limits/" + saved.getId()))
                .body(ConsumptionLimitDto.from(saved));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ConsumptionLimitDto>> listAllPageable(Pageable pageable) {
        return ResponseEntity.ok(
                service.findAll(pageable).map(ConsumptionLimitDto::from));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsumptionLimitDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ConsumptionLimitDto.from(service.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsumptionLimitDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody ConsumptionLimit limit
    ) {
        return ResponseEntity.ok(
                ConsumptionLimitDto.from(service.update(id, limit)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/monthly-recalculation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConsumptionLimitDto>> recalculateMonthly(
            @RequestParam(value = "yearMonth", required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        var limits = service.recalculateMonthlyLimits(yearMonth);
        var dtos   = limits.stream().map(ConsumptionLimitDto::from).toList();
        YearMonth ym = (yearMonth != null ? yearMonth : YearMonth.now().minusMonths(1));
        URI uri = URI.create("/api/limits/monthly-recalculation?yearMonth=" + ym);
        return ResponseEntity.created(uri).body(dtos);
    }

    // SHARED / OWNER

    @GetMapping("/equipment/{equipmentId}")
    @PreAuthorize("hasRole('ADMIN') or @equipmentService.isOwner(#equipmentId, principal)")
    public ResponseEntity<ConsumptionLimitDto> getByEquipment(
            @PathVariable Long equipmentId) {

        return ResponseEntity.ok(
                ConsumptionLimitDto.from(service.findByEquipment(equipmentId)));
    }

    // USER

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ConsumptionLimitDto>> myLimits(
            @AuthenticationPrincipal br.com.fiap.voltly.domain.model.User principal) {
        var dtos = service.findAllByOwner(principal.getId())
                .stream()
                .map(ConsumptionLimitDto::from)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}

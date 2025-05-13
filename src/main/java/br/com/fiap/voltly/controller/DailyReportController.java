package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.Co2ReportDto;
import br.com.fiap.voltly.domain.dto.DailyReportDto;
import br.com.fiap.voltly.domain.model.DailyReport;
import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.service.DailyReportService;
import br.com.fiap.voltly.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports/daily-consumption")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService service;
    private final EquipmentService   equipmentService;

    // ADMIN

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DailyReportDto>> generate(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        List<DailyReport> list = service.generateDailyConsumptionReport(d);
        var dtos = list.stream().map(DailyReportDto::from).toList();
        return ResponseEntity
                .created(URI.create("/api/reports/daily-consumption?date=" + d))
                .body(dtos);
    }

    // ALL LOGGED IN USERS

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #equipmentId != null and @equipmentService.isOwner(#equipmentId, principal))"
    )
    public ResponseEntity<List<DailyReportDto>> fetchAll(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId,
            @AuthenticationPrincipal User principal
    ) {
        List<DailyReport> list = service.getReportsByDateAndEquipment(date, equipmentId);
        var dtos = list.stream().map(DailyReportDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/co2")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #equipmentId != null and @equipmentService.isOwner(#equipmentId, principal))")
    public ResponseEntity<List<Co2ReportDto>> fetchCo2All(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId,
            @AuthenticationPrincipal User principal
    ) {
        var dtos = service.getDailyCo2(date, equipmentId);
        return ResponseEntity.ok(dtos);
    }
}

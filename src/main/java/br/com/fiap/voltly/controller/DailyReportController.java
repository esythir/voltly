package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.Co2ReportDto;
import br.com.fiap.voltly.domain.dto.DailyReportDto;
import br.com.fiap.voltly.domain.model.DailyReport;
import br.com.fiap.voltly.domain.repository.DailyReportRepository;
import br.com.fiap.voltly.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports/daily-consumption")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService service;
    private final DailyReportRepository repo;

    @PostMapping
    public ResponseEntity<List<DailyReportDto>> generate(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate d = (date != null) ? date : LocalDate.now();
        List<DailyReport> list = service.generateDailyConsumptionReport(d);
        List<DailyReportDto> dtos = list.stream()
                .map(DailyReportDto::from)
                .collect(Collectors.toList());
        URI uri = URI.create("/api/reports/daily-consumption?date=" + d);
        return ResponseEntity.created(uri).body(dtos);
    }

    @GetMapping
    public ResponseEntity<List<DailyReportDto>> fetch(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "equipmentId", required = false) Long eqId
    ) {
        var list = service.getReportsByDateAndEquipment(date, eqId);
        var dtos = list.stream().map(DailyReportDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/co2")
    public ResponseEntity<List<Co2ReportDto>> getDailyCo2(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long equipmentId
    ) {
        List<Co2ReportDto> dtos = service.getDailyCo2(date, equipmentId);
        return ResponseEntity.ok(dtos);
    }
}

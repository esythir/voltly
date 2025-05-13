package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.AlertDto;
import br.com.fiap.voltly.domain.model.Alert;
import br.com.fiap.voltly.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;

    @PostMapping
    public ResponseEntity<List<AlertDto>> generate(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId
    ) {
        List<Alert> created = service.generateAlerts(date, equipmentId);
        List<AlertDto> dtos = created.stream()
                .map(AlertDto::from)
                .collect(Collectors.toList());

        LocalDate d = (date != null ? date : LocalDate.now());
        URI uri = URI.create("/api/alerts?date=" + d +
                (equipmentId != null ? "&equipmentId=" + equipmentId : ""));
        return ResponseEntity.created(uri).body(dtos);
    }

    @GetMapping
    public ResponseEntity<List<AlertDto>> fetch(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId
    ) {
        List<Alert> list = service.getAlerts(date, equipmentId);
        List<AlertDto> dtos = list.stream()
                .map(AlertDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

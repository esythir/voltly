package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.AlertDto;
import br.com.fiap.voltly.domain.model.Alert;
import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.service.AlertService;
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
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService service;
    private final EquipmentService equipmentService;

    // ADMIN

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlertDto>> generateAlert(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId
    ) {
        List<Alert> created = service.generateAlerts(date, equipmentId);
        var dtos = created.stream().map(AlertDto::from).toList();
        URI uri = URI.create("/api/alerts?date="
                + (date != null ? date : LocalDate.now())
                + (equipmentId != null ? "&equipmentId=" + equipmentId : ""));
        return ResponseEntity.created(uri).body(dtos);
    }

    // ALL LOGGED IN USERS

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #equipmentId != null and @equipmentService.isOwner(#equipmentId, principal))"
    )
    public ResponseEntity<List<AlertDto>> fetchAll(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "equipmentId", required = false) Long equipmentId,
            @AuthenticationPrincipal User principal
    ) {
        List<Alert> list = service.getAlerts(date, equipmentId);
        var dtos = list.stream().map(AlertDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

    // USER

    @GetMapping("/me")
    @PreAuthorize(
            "hasRole('USER') and #equipmentId != null and @equipmentService.isOwner(#equipmentId, principal)"
    )
    public ResponseEntity<List<AlertDto>> fetchMine(
            @AuthenticationPrincipal User principal,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("equipmentId") Long equipmentId
    ) {
        List<Alert> list = service.getAlerts(date, equipmentId);
        var dtos = list.stream().map(AlertDto::from).toList();
        return ResponseEntity.ok(dtos);
    }
}

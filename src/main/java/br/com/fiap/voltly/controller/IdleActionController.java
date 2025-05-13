package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.AutomaticActionDto;
import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.service.IdleActionService;
import br.com.fiap.voltly.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/idle-actions")
@RequiredArgsConstructor
public class IdleActionController {

    private final IdleActionService idleService;
    private final EquipmentService   equipmentService;

    // ADMIN

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AutomaticActionDto>> detectAllIdle(
            @RequestParam(defaultValue = "0.5") double thresholdKw,
            @RequestParam(defaultValue = "0")   double occupancyPercentage,
            @RequestParam(defaultValue = "10")  int windowMinutes,
            @RequestParam(required = false)     Long equipmentId
    ) {
        var actions = idleService.detectIdleActions(thresholdKw, occupancyPercentage, windowMinutes, equipmentId);
        var dtos    = actions.stream().map(AutomaticActionDto::from).toList();
        return ResponseEntity.ok(dtos);
    }

    // SHARED / OWNER

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') and #equipmentId != null and @equipmentService.isOwner(#equipmentId, principal)"
    )
    public ResponseEntity<List<AutomaticActionDto>> detectMyIdle(
            @AuthenticationPrincipal User principal,
            @RequestParam(defaultValue = "0.5") double thresholdKw,
            @RequestParam(defaultValue = "0")   double occupancyPercentage,
            @RequestParam(defaultValue = "10")  int windowMinutes,
            @RequestParam                          Long equipmentId
    ) {
        var actions = idleService.detectIdleActions(thresholdKw, occupancyPercentage, windowMinutes, equipmentId);
        var dtos    = actions.stream().map(AutomaticActionDto::from).toList();
        return ResponseEntity.ok(dtos);
    }
}

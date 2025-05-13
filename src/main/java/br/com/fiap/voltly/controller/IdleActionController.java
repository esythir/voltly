package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.AutomaticActionDto;
import br.com.fiap.voltly.service.IdleActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/idle-actions")
@RequiredArgsConstructor
public class IdleActionController {

    private final IdleActionService idleService;

    @GetMapping
    public ResponseEntity<List<AutomaticActionDto>> detectIdle(
            @RequestParam(defaultValue = "0.5") double thresholdKw,
            @RequestParam(defaultValue = "0") double occupancyPercentage,
            @RequestParam(defaultValue = "10") int windowMinutes,
            @RequestParam(required = false) Long equipmentId
    ) {
        var actions = idleService
                .detectIdleActions(thresholdKw, occupancyPercentage, windowMinutes, equipmentId);

        var dtos = actions.stream()
                .map(AutomaticActionDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}

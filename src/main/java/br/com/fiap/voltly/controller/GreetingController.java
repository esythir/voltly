package br.com.fiap.voltly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GreetingController {

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> greeting() {
        return ResponseEntity.ok("Welcome to Voltly API!");
    }

}

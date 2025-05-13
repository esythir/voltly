package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.UserCreateDto;
import br.com.fiap.voltly.domain.dto.UserDto;
import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.service.UserService;
import br.com.fiap.voltly.utils.DTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // ADMIN

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserCreateDto dto
    ) {
        User user = dto.toModel();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User created = userService.save(user);
        URI uri = URI.create("/api/users/" + created.getId());
        return ResponseEntity
                .created(uri)
                .body(UserDto.from(created));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> listAllPageable(Pageable pageable) {
        Page<UserDto> page = DTOMapper.mapPage(userService.findAll(pageable), UserDto::from);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(UserDto.from(userService.findById(id)));
    }

    @GetMapping(params = "email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(UserDto.from(userService.findByEmail(email)));
    }

    @GetMapping(params = {"birthDateAfter", "birthDateBefore"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByBirthDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateAfter,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDateBefore) {
        List<UserDto> list = userService.findByBirthDateBetween(birthDateAfter, birthDateBefore)
                .stream().map(UserDto::from).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> searchByName(@RequestParam String name) {
        List<UserDto> list = userService.searchByName(name)
                .stream().map(UserDto::from).toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public ResponseEntity<UserDto> updateById(
            @PathVariable Long id,
            @Valid @RequestBody User u
    ) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return ResponseEntity.ok(UserDto.from(userService.update(id, u)));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        userService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // USER

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal User principal) {
        return ResponseEntity.ok(UserDto.from(userService.findById(principal.getId())));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> updateMe(
            @AuthenticationPrincipal User principal,
            @Valid @RequestBody UserCreateDto dto
    ) {
        User u = dto.toModel();
        u.setId(principal.getId());
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return ResponseEntity.ok(UserDto.from(userService.update(principal.getId(), u)));
    }

}

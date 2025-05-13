package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.domain.dto.UserCreateDto;
import br.com.fiap.voltly.domain.dto.UserDto;
import br.com.fiap.voltly.domain.dto.UserSummaryDto;
import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.service.UserService;
import br.com.fiap.voltly.utils.DTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody UserCreateDto dto
    ) {
        var userEntity = dto.toModel();
        var created    = userService.save(userEntity);
        URI uri = URI.create("/api/users/" + created.getId());
        return ResponseEntity
                .created(uri)
                .body(UserDto.from(created));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> dtoPage = DTOMapper.mapPage(
                userService.findAll(pageable),
                UserDto::from
        );
        return ResponseEntity.ok(dtoPage);
    }



    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserDto.from(userService.findById(id)));
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(UserDto.from(userService.findByEmail(email)));
    }

    @GetMapping(params = {"birthDateAfter", "birthDateBefore"})
    public ResponseEntity<List<UserDto>> getUsersByBirthDateRange(
            @RequestParam("birthDateAfter") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @RequestParam("birthDateBefore") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before
    ) {
        List<UserDto> dtos = userService.findByBirthDateBetween(after, before).stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String name) {
        List<UserDto> dtos = userService.searchByName(name)
                .stream().map(UserDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user
    ) {
        return ResponseEntity.ok(UserDto.from(userService.update(id, user)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

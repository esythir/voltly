package br.com.fiap.voltly.controller;

import br.com.fiap.voltly.config.security.TokenService;
import br.com.fiap.voltly.domain.dto.LoginDto;
import br.com.fiap.voltly.domain.dto.RegisterDto;
import br.com.fiap.voltly.domain.dto.TokenDto;
import br.com.fiap.voltly.domain.dto.UserDto;
import br.com.fiap.voltly.domain.model.User;
import br.com.fiap.voltly.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final TokenService          tokenService;
    private final UserService           userService;
    private final PasswordEncoder       passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterDto dto) {
        String encoded = passwordEncoder.encode(dto.password());
        User saved = userService.save(dto.toModel(encoded));
        return ResponseEntity
                .created(URI.create("/auth/register"))
                .body(UserDto.from(saved));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto dto){
        var creds = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        Authentication auth = authManager.authenticate(creds);
        String token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new TokenDto(token));
    }

}

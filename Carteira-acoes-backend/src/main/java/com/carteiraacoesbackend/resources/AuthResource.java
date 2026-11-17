package com.carteiraacoesbackend.resources;

import com.carteiraacoesbackend.dto.LoginRequest;
import com.carteiraacoesbackend.dto.TokenResponse;
import com.carteiraacoesbackend.dto.AuthMeResponse;
import com.carteiraacoesbackend.services.AuthService;
import com.carteiraacoesbackend.services.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthResource(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> me(Authentication authentication) {
        var usuario = currentUserService.required();
        List<String> authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();
        return ResponseEntity.ok(new AuthMeResponse(usuario.getId(), usuario.getEmail(), authorities));
    }
}

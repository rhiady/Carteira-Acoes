package com.carteiraacoesbackend.config;

import com.carteiraacoesbackend.domains.Usuario;
import com.carteiraacoesbackend.domains.enums.Papel;
import com.carteiraacoesbackend.repositories.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class AdminInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String senha;

    public AdminInitializer(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.email}") String email,
            @Value("${app.bootstrap.admin.senha}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.senha = senha;
    }

    @PostConstruct
    public void criarAdministradorInicial() {
        if (email.isBlank() || senha.isBlank()) {
            throw new IllegalArgumentException(
                    "APP_ADMIN_EMAIL e APP_ADMIN_SENHA devem ser informados"
            );
        }

        String emailNormalizado = email.trim().toLowerCase();
        if (usuarioRepository.findByEmail(emailNormalizado).isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setEmail(emailNormalizado);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setPapel(Papel.ADMIN);
        usuarioRepository.save(admin);
    }
}

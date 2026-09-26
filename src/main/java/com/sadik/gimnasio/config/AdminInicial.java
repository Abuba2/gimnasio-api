package com.sadik.gimnasio.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sadik.gimnasio.model.Rol;
import com.sadik.gimnasio.model.Usuario;
import com.sadik.gimnasio.repository.UsuarioRepository;

@Component
public class AdminInicial implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminInicial(UsuarioRepository repository, PasswordEncoder passwordEncoder,
            @Value("${admin.email:}") String email,
            @Value("${admin.password:}") String password) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() || password.isBlank()) {
            log.info("No se ha definido ADMIN_EMAIL/ADMIN_PASSWORD: no se crea administrador inicial");
            return;
        }

        if (repository.existsByEmail(email)) {
            return;
        }

        repository.save(new Usuario(email, passwordEncoder.encode(password), Rol.ADMIN));
        log.info("Administrador inicial creado: {}", email);
    }
}

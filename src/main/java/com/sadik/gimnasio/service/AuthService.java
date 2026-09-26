package com.sadik.gimnasio.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sadik.gimnasio.controller.dto.AuthResponse;
import com.sadik.gimnasio.exception.EmailDuplicadoException;
import com.sadik.gimnasio.model.Rol;
import com.sadik.gimnasio.model.Usuario;
import com.sadik.gimnasio.repository.UsuarioRepository;
import com.sadik.gimnasio.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository repository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(String email, String password) {
        if (repository.existsByEmail(email)) {
            throw new EmailDuplicadoException(email);
        }

        Usuario usuario = new Usuario(email, passwordEncoder.encode(password), Rol.USER);
        repository.save(usuario);

        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        Usuario usuario = repository.findByEmail(email).orElseThrow();

        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name());
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name());
    }
}

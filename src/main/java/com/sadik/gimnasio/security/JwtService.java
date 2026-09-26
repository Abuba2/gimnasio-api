package com.sadik.gimnasio.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey clave;
    private final long duracionMillis;

    public JwtService(
            @Value("${jwt.secret}") String secreto,
            @Value("${jwt.expiration-ms}") long duracionMillis) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes());
        this.duracionMillis = duracionMillis;
    }

    public String generarToken(String email, String rol) {
        Date ahora = new Date();
        Date caducidad = new Date(ahora.getTime() + duracionMillis);

        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(caducidad)
                .signWith(clave)
                .compact();
    }

    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    public boolean esValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

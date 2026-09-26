package com.sadik.gimnasio.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({ SalaNoEncontradaException.class,
                        MonitorNoEncontradoException.class,
                        SocioNoEncontradoException.class,
                        ClaseNoEncontradaException.class,
                        ReservaNoEncontradaException.class })
    public ResponseEntity<ErrorResponse> noEncontrada(RuntimeException ex, WebRequest req) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler({ NombreDeSalaDuplicadoException.class,
                        EmailDuplicadoException.class,
                        ReservaDuplicadaException.class,
                        ClaseCompletaException.class,
                        SocioInactivoException.class,
                        ClaseYaPasadaException.class })
    public ResponseEntity<ErrorResponse> duplicado(RuntimeException ex, WebRequest req) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacionFallida(MethodArgumentNotValidException ex,
            WebRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse cuerpo = ErrorResponse.deValidacion(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Los datos enviados no son validos",
                req.getDescription(false).replace("uri=", ""),
                campos);

        return ResponseEntity.badRequest().body(cuerpo);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> argumentoInvalido(IllegalArgumentException ex, WebRequest req) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> rutaNoExiste(NoResourceFoundException ex, WebRequest req) {
        return construir(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> credencialesInvalidas(AuthenticationException ex, WebRequest req) {
        return construir(HttpStatus.UNAUTHORIZED, "Email o contrasena incorrectos", req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accesoDenegado(AccessDeniedException ex, WebRequest req) {
        return construir(HttpStatus.FORBIDDEN, "No tienes permisos para esta operacion", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> errorInesperado(Exception ex, WebRequest req) {
        log.error("Error no controlado en {}", req.getDescription(false), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error inesperado", req);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus estado, String mensaje, WebRequest req) {
        ErrorResponse cuerpo = ErrorResponse.of(
                estado.value(),
                estado.getReasonPhrase(),
                mensaje,
                req.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(estado).body(cuerpo);
    }
}

package com.sadik.gimnasio.exception;

public class EmailDuplicadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EmailDuplicadoException(String email) {
        super("Ya existe un usuario registrado con el email: " + email);
    }
}

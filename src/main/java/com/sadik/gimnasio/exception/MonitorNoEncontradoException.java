package com.sadik.gimnasio.exception;

public class MonitorNoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MonitorNoEncontradoException(Long id) {
        super("No existe el monitor con id " + id);
    }
}

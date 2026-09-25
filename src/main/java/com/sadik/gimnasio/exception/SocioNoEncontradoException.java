package com.sadik.gimnasio.exception;

public class SocioNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SocioNoEncontradoException(Long id) {
        super("No existe el socio con id " + id);
    }
}

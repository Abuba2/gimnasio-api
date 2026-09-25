package com.sadik.gimnasio.exception;

public class NombreDeSalaDuplicadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NombreDeSalaDuplicadoException(String nombre) {
        super("Ya existe una sala con el nombre: " + nombre);
    }
}

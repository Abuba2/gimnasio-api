package com.sadik.gimnasio.exception;

public class ClaseCompletaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClaseCompletaException(Long claseId, int aforo) {
        super("La clase " + claseId + " esta completa (aforo " + aforo + ")");
    }
}

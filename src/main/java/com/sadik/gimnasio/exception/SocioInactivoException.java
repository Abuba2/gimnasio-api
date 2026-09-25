package com.sadik.gimnasio.exception;

public class SocioInactivoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SocioInactivoException(Long socioId) {
        super("El socio " + socioId + " esta de baja y no puede reservar");
    }
}

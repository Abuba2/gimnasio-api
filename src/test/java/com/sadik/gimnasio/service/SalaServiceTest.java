package com.sadik.gimnasio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sadik.gimnasio.exception.NombreDeSalaDuplicadoException;
import com.sadik.gimnasio.exception.SalaNoEncontradaException;
import com.sadik.gimnasio.model.Sala;
import com.sadik.gimnasio.repository.SalaRepository;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository repository;

    @InjectMocks
    private SalaService service;

    @Test
    void rechazaAforoCeroONegativo() {
        assertThatThrownBy(() -> service.crear("Sala nueva", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mayor que 0");

        verify(repository, never()).save(any(Sala.class));
    }

    @Test
    void rechazaNombreDuplicado() {
        when(repository.existsByNombre("Piscina")).thenReturn(true);

        assertThatThrownBy(() -> service.crear("Piscina", 20))
                .isInstanceOf(NombreDeSalaDuplicadoException.class);

        verify(repository, never()).save(any(Sala.class));
    }

    @Test
    void lanzaExcepcionSiLaSalaNoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(1L))
                .isInstanceOf(SalaNoEncontradaException.class);
    }

    @Test
    void guardaLaSalaCuandoLosDatosSonValidos() {
        when(repository.existsByNombre("Piscina")).thenReturn(false);
        when(repository.save(any(Sala.class))).thenAnswer(inv -> inv.getArgument(0));

        Sala creada = service.crear("Piscina", 15);

        assertThat(creada.getNombre()).isEqualTo("Piscina");
        assertThat(creada.getAforo()).isEqualTo(15);
        verify(repository).save(any(Sala.class));
    }
}

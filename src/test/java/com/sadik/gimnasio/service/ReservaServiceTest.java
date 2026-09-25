package com.sadik.gimnasio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sadik.gimnasio.exception.ClaseCompletaException;
import com.sadik.gimnasio.exception.ClaseYaPasadaException;
import com.sadik.gimnasio.exception.ReservaDuplicadaException;
import com.sadik.gimnasio.exception.SocioInactivoException;
import com.sadik.gimnasio.model.Clase;
import com.sadik.gimnasio.model.EstadoReserva;
import com.sadik.gimnasio.model.Reserva;
import com.sadik.gimnasio.model.Socio;
import com.sadik.gimnasio.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository repository;

    @Mock
    private SocioService socioService;

    @Mock
    private ClaseService claseService;

    @InjectMocks
    private ReservaService service;

    private Socio socioActivo() {
        return new Socio("Ana", "ana@mail.com");
    }

    private Socio socioDeBaja() {
        Socio socio = new Socio("Luis", "luis@mail.com");
        socio.setActivo(false);
        return socio;
    }

    private Clase claseFutura(int aforo) {
        return new Clase("Spinning", LocalDateTime.now().plusDays(7), aforo, null, null);
    }

    private Clase clasePasada() {
        return new Clase("Spinning", LocalDateTime.now().minusDays(1), 20, null, null);
    }

    @Test
    void rechazaSocioDeBaja() {
        when(socioService.buscarPorId(1L)).thenReturn(socioDeBaja());
        when(claseService.buscarPorId(2L)).thenReturn(claseFutura(20));

        assertThatThrownBy(() -> service.reservar(1L, 2L))
                .isInstanceOf(SocioInactivoException.class);

        verify(repository, never()).save(any(Reserva.class));
    }

    @Test
    void rechazaClaseQueYaHaPasado() {
        when(socioService.buscarPorId(1L)).thenReturn(socioActivo());
        when(claseService.buscarPorId(2L)).thenReturn(clasePasada());

        assertThatThrownBy(() -> service.reservar(1L, 2L))
                .isInstanceOf(ClaseYaPasadaException.class);

        verify(repository, never()).save(any(Reserva.class));
    }

    @Test
    void rechazaReservaDuplicada() {
        when(socioService.buscarPorId(1L)).thenReturn(socioActivo());
        when(claseService.buscarPorId(2L)).thenReturn(claseFutura(20));
        when(repository.existsBySocioAndClaseAndEstado(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.reservar(1L, 2L))
                .isInstanceOf(ReservaDuplicadaException.class);

        verify(repository, never()).save(any(Reserva.class));
    }

    @Test
    void rechazaClaseCompleta() {
        when(socioService.buscarPorId(1L)).thenReturn(socioActivo());
        when(claseService.buscarPorId(2L)).thenReturn(claseFutura(1));
        when(repository.existsBySocioAndClaseAndEstado(any(), any(), any())).thenReturn(false);
        when(repository.countByClaseAndEstado(any(), any())).thenReturn(1L);

        assertThatThrownBy(() -> service.reservar(1L, 2L))
                .isInstanceOf(ClaseCompletaException.class);

        verify(repository, never()).save(any(Reserva.class));
    }

    @Test
    void creaLaReservaCuandoTodoEsValido() {
        when(socioService.buscarPorId(1L)).thenReturn(socioActivo());
        when(claseService.buscarPorId(2L)).thenReturn(claseFutura(20));
        when(repository.existsBySocioAndClaseAndEstado(any(), any(), any())).thenReturn(false);
        when(repository.countByClaseAndEstado(any(), any())).thenReturn(5L);
        when(repository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva reserva = service.reservar(1L, 2L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(reserva.getSocio().getNombre()).isEqualTo("Ana");
        assertThat(reserva.getClase().getNombre()).isEqualTo("Spinning");
        verify(repository).save(any(Reserva.class));
    }

    @Test
    void permiteReservarCuandoQuedaLaUltimaPlaza() {
        when(socioService.buscarPorId(1L)).thenReturn(socioActivo());
        when(claseService.buscarPorId(2L)).thenReturn(claseFutura(20));
        when(repository.existsBySocioAndClaseAndEstado(any(), any(), any())).thenReturn(false);
        when(repository.countByClaseAndEstado(any(), any())).thenReturn(19L);
        when(repository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva reserva = service.reservar(1L, 2L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
    }
}

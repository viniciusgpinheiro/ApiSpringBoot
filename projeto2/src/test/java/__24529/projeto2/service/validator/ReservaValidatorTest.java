package __24529.projeto2.service.validator;

import __24529.projeto2.exceptions.OperacaoNaoPermitidaException;
import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Laboratorio;
import __24529.projeto2.model.Reserva;
import __24529.projeto2.model.Sala;
import __24529.projeto2.model.Status;
import __24529.projeto2.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservaValidatorTest {

    @Mock
    private ReservaRepository reservaRepository;

    private ReservaValidator reservaValidator;

    private Sala sala;
    private Status statusAtiva;

    @BeforeEach
    void setUp() {
        reservaValidator = new ReservaValidator(reservaRepository);

        sala = new Sala();
        sala.setCodigo(1);
        sala.setNome("52998224725");
        sala.setCapacidade(30);
        sala.setLocalizacao("Bloco A - Andar 1");

        statusAtiva = new Status();
        statusAtiva.setCodigo(1);
        statusAtiva.setNome("STATUS_RESERVA_ATIVA");
        statusAtiva.setTipo("RESERVA");
    }

    private Reserva novaReserva(LocalDate dataInicial, LocalDate dataFinal, LocalTime horaInicial, LocalTime horaFinal) {
        Reserva reserva = new Reserva();
        reserva.setDataInicial(dataInicial);
        reserva.setDataFinal(dataFinal);
        reserva.setTempoInicial(horaInicial);
        reserva.setTempoFinal(horaFinal);
        reserva.setSala(sala);
        reserva.setStatus(statusAtiva);
        return reserva;
    }

    @Test
    void deveRejeitarReservaDeMultiplosDias() {
        Reserva reserva = novaReserva(LocalDate.now().plusDays(5), LocalDate.now().plusDays(6),
                LocalTime.of(10, 0, 0), LocalTime.of(11, 0, 0));
        when(reservaRepository.findBySala_CodigoAndDataInicial(anyInt(), any())).thenReturn(List.of());

        OperacaoNaoPermitidaException ex = assertThrows(OperacaoNaoPermitidaException.class,
                () -> reservaValidator.validar(reserva));
        assertEquals("A reserva é diária/por dia", ex.getMessage());
    }

    @Test
    void deveRejeitarHoraFinalMenorOuIgualHoraInicial() {
        LocalDate dia = LocalDate.now().plusDays(5);
        Reserva reserva = novaReserva(dia, dia, LocalTime.of(11, 0, 0), LocalTime.of(11, 0, 0));

        OperacaoNaoPermitidaException ex = assertThrows(OperacaoNaoPermitidaException.class,
                () -> reservaValidator.validar(reserva));
        assertEquals("Hora Final precisa ser maior ou igual à Hora Inicial", ex.getMessage());
    }

    @Test
    void deveRejeitarQuandoNenhumRecursoInformado() {
        LocalDate dia = LocalDate.now().plusDays(5);
        Reserva reserva = novaReserva(dia, dia, LocalTime.of(10, 0, 0), LocalTime.of(11, 0, 0));
        reserva.setSala(null);

        assertThrows(OperacaoNaoPermitidaException.class, () -> reservaValidator.validar(reserva));
    }

    @Test
    void deveRejeitarRecursoBloqueado() {
        Status bloqueado = new Status();
        bloqueado.setNome("BLOQUEADO");
        sala.setStatus(bloqueado);

        LocalDate dia = LocalDate.now().plusDays(5);
        Reserva reserva = novaReserva(dia, dia, LocalTime.of(10, 0, 0), LocalTime.of(11, 0, 0));

        assertThrows(OperacaoNaoPermitidaException.class, () -> reservaValidator.validar(reserva));
    }

    @Test
    void deveDetectarConflitoDeHorario() {
        LocalDate dia = LocalDate.now().plusDays(5);
        Reserva nova = novaReserva(dia, dia, LocalTime.of(10, 30, 0), LocalTime.of(11, 30, 0));

        Reserva existente = novaReserva(dia, dia, LocalTime.of(10, 0, 0), LocalTime.of(11, 0, 0));
        existente.setId(99);

        when(reservaRepository.findBySala_CodigoAndDataInicial(sala.getCodigo(), dia))
                .thenReturn(List.of(existente));

        assertThrows(RegistroDuplicadoException.class, () -> reservaValidator.validar(nova));
    }

    @Test
    void naoDeveDetectarConflitoQuandoHorariosNaoSeSobrepoem() {
        LocalDate dia = LocalDate.now().plusDays(5);
        Reserva nova = novaReserva(dia, dia, LocalTime.of(11, 0, 0), LocalTime.of(12, 0, 0));

        Reserva existente = novaReserva(dia, dia, LocalTime.of(10, 0, 0), LocalTime.of(11, 0, 0));
        existente.setId(99);

        when(reservaRepository.findBySala_CodigoAndDataInicial(sala.getCodigo(), dia))
                .thenReturn(List.of(existente));

        assertDoesNotThrow(() -> reservaValidator.validar(nova));
    }

    @Test
    void naoDeveConsiderarConflitoComReservaCancelada() {
        LocalDate dia = LocalDate.now().plusDays(5);
        Reserva nova = novaReserva(dia, dia, LocalTime.of(10, 0, 0), LocalTime.of(11, 0, 0));

        Status cancelada = new Status();
        cancelada.setNome("CANCELADA");
        Reserva existente = novaReserva(dia, dia, LocalTime.of(10, 0, 0), LocalTime.of(11, 0, 0));
        existente.setId(99);
        existente.setStatus(cancelada);

        when(reservaRepository.findBySala_CodigoAndDataInicial(sala.getCodigo(), dia))
                .thenReturn(List.of(existente));

        assertDoesNotThrow(() -> reservaValidator.validar(nova));
    }

    @Test
    void deveRejeitarCancelamentoComMenosDe24Horas() {
        Reserva reserva = new Reserva();
        LocalDateTime daqui10h = LocalDateTime.now().plusHours(10);
        reserva.setDataInicial(daqui10h.toLocalDate());
        reserva.setTempoInicial(daqui10h.toLocalTime());

        assertThrows(OperacaoNaoPermitidaException.class, () -> reservaValidator.validarCancelamento(reserva));
    }

    @Test
    void devePermitirCancelamentoComMaisDe24Horas() {
        Reserva reserva = new Reserva();
        LocalDateTime daqui48h = LocalDateTime.now().plusHours(48);
        reserva.setDataInicial(daqui48h.toLocalDate());
        reserva.setTempoInicial(daqui48h.toLocalTime());

        assertDoesNotThrow(() -> reservaValidator.validarCancelamento(reserva));
    }
}

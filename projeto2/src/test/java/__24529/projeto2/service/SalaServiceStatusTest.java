package __24529.projeto2.service;

import __24529.projeto2.model.Reserva;
import __24529.projeto2.model.Sala;
import __24529.projeto2.model.Status;
import __24529.projeto2.repository.ReservaRepository;
import __24529.projeto2.repository.SalaRepository;
import __24529.projeto2.repository.StatusRepository;
import __24529.projeto2.service.validator.SalaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaServiceStatusTest {

    @Mock
    private SalaRepository salaRepository;
    @Mock
    private SalaValidator salaValidator;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private ReservaRepository reservaRepository;

    private SalaService salaService;

    private Sala novaSala() {
        SalaService service = new SalaService(salaRepository, salaValidator, statusRepository, reservaRepository);
        this.salaService = service;
        Sala sala = new Sala();
        sala.setCodigo(1);
        sala.setNome("52998224725");
        sala.setCapacidade(20);
        sala.setLocalizacao("Bloco B - Andar 2");
        return sala;
    }

    @Test
    void devePermanecerBloqueadaIndependenteDeReserva() {
        Sala sala = novaSala();
        Status bloqueado = new Status();
        bloqueado.setNome("BLOQUEADO");
        sala.setStatus(bloqueado);

        assertEquals("BLOQUEADO", salaService.calcularStatusAtual(sala));
    }

    @Test
    void deveEstarOcupadaQuandoExisteReservaAtivaAgora() {
        Sala sala = novaSala();

        Status ativa = new Status();
        ativa.setNome("ATIVA");

        Reserva reserva = new Reserva();
        reserva.setStatus(ativa);
        reserva.setTempoInicial(LocalTime.now().minusMinutes(5));
        reserva.setTempoFinal(LocalTime.now().plusMinutes(55));

        when(reservaRepository.findBySala_CodigoAndDataInicial(1, LocalDate.now()))
                .thenReturn(List.of(reserva));

        assertEquals("OCUPADO", salaService.calcularStatusAtual(sala));
    }

    @Test
    void deveEstarLivreQuandoNaoExisteReservaAtivaAgora() {
        Sala sala = novaSala();

        when(reservaRepository.findBySala_CodigoAndDataInicial(1, LocalDate.now()))
                .thenReturn(List.of());

        assertEquals("LIVRE", salaService.calcularStatusAtual(sala));
    }
}

package __24529.projeto2.service.validator;

import __24529.projeto2.exceptions.OperacaoNaoPermitidaException;
import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Reserva;
import __24529.projeto2.repository.ReservaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservaValidator {

    private final ReservaRepository reservaRepository;

    public ReservaValidator(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public void validar(Reserva reserva) {
        // Data Final deve ser >= Data Inicial (mensagem literal do enunciado)
        if (reserva.getDataFinal().isBefore(reserva.getDataInicial())) {
            throw new OperacaoNaoPermitidaException(
                    "Data Final precisa ser maior ou igual à Data Inicial");
        }

        // Reserva estritamente diária: não são permitidas reservas de múltiplos dias
        if (!reserva.getDataInicial().isEqual(reserva.getDataFinal())) {
            throw new OperacaoNaoPermitidaException(
                    "A reserva é diária/por dia");
        }

        // Hora Final deve ser > Hora Inicial (mesma data) (mensagem literal do enunciado)
        if (!reserva.getTempoFinal().isAfter(reserva.getTempoInicial())) {
            throw new OperacaoNaoPermitidaException(
                    "Hora Final precisa ser maior ou igual à Hora Inicial");
        }

        boolean temSala = reserva.getSala() != null;
        boolean temLaboratorio = reserva.getLaboratorio() != null;

        // Deve ser informado exatamente um recurso: sala OU laboratório
        if (temSala == temLaboratorio) {
            throw new OperacaoNaoPermitidaException(
                    "Informe exatamente um recurso para a reserva: uma Sala OU um Laboratório.");
        }

        // Recurso não pode estar BLOQUEADO
        if (temSala && reserva.getSala().getStatus() != null
                && "BLOQUEADO".equalsIgnoreCase(reserva.getSala().getStatus().getNome())) {
            throw new OperacaoNaoPermitidaException(
                    "A sala informada está BLOQUEADA (em manutenção) e não pode ser reservada.");
        }
        if (temLaboratorio && reserva.getLaboratorio().getStatus() != null
                && "BLOQUEADO".equalsIgnoreCase(reserva.getLaboratorio().getStatus().getNome())) {
            throw new OperacaoNaoPermitidaException(
                    "O laboratório informado está BLOQUEADO (em manutenção) e não pode ser reservado.");
        }

        // Verificação de conflito de horário com outras reservas ATIVAS do mesmo recurso
        List<Reserva> reservasDoRecurso = temSala
                ? reservaRepository.findBySala_CodigoAndDataInicial(reserva.getSala().getCodigo(), reserva.getDataInicial())
                : reservaRepository.findByLaboratorio_CodigoAndDataInicial(reserva.getLaboratorio().getCodigo(), reserva.getDataInicial());

        boolean existeConflito = reservasDoRecurso.stream()
                .filter(r -> reserva.getId() == null || !r.getId().equals(reserva.getId()))
                .filter(r -> r.getStatus() == null || !"CANCELADA".equalsIgnoreCase(r.getStatus().getNome()))
                .anyMatch(r -> haSobreposicaoDeHorario(reserva, r));

        if (existeConflito) {
            throw new RegistroDuplicadoException(
                    "Já existe uma reserva ativa para este recurso no período informado.");
        }
    }

    private boolean haSobreposicaoDeHorario(Reserva nova, Reserva existente) {
        // Não há sobreposição se uma termina antes/quando a outra começa
        boolean semSobreposicao = !nova.getTempoInicial().isBefore(existente.getTempoFinal())
                || !existente.getTempoInicial().isBefore(nova.getTempoFinal());
        return !semSobreposicao;
    }

    // Cancelamento permitido apenas até 24 horas antes da data/hora reservada
    public void validarCancelamento(Reserva reserva) {
        LocalDateTime inicioDaReserva = LocalDateTime.of(reserva.getDataInicial(), reserva.getTempoInicial());
        if (LocalDateTime.now().isAfter(inicioDaReserva.minusHours(24))) {
            throw new OperacaoNaoPermitidaException(
                    "O cancelamento só é permitido até 24 horas antes da data e hora reservadas.");
        }
    }
}

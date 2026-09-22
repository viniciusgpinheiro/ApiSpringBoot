package __24529.projeto2.service;

import __24529.projeto2.controler.dto.ReservaDTO;
import __24529.projeto2.exceptions.OperacaoNaoPermitidaException;
import __24529.projeto2.exceptions.RegistroNaoEncontradoException;
import __24529.projeto2.model.Laboratorio;
import __24529.projeto2.model.Reserva;
import __24529.projeto2.model.Sala;
import __24529.projeto2.model.Status;
import __24529.projeto2.model.Usuario;
import __24529.projeto2.repository.LaboratorioRepository;
import __24529.projeto2.repository.ReservaRepository;
import __24529.projeto2.repository.SalaRepository;
import __24529.projeto2.repository.StatusRepository;
import __24529.projeto2.repository.UsuarioRepository;
import __24529.projeto2.service.validator.ReservaValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalaRepository salaRepository;
    private final LaboratorioRepository laboratorioRepository;
    private final StatusRepository statusRepository;
    private final ReservaValidator reservaValidator;

    public ReservaService(ReservaRepository reservaRepository,
                           UsuarioRepository usuarioRepository,
                           SalaRepository salaRepository,
                           LaboratorioRepository laboratorioRepository,
                           StatusRepository statusRepository,
                           ReservaValidator reservaValidator) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.salaRepository = salaRepository;
        this.laboratorioRepository = laboratorioRepository;
        this.statusRepository = statusRepository;
        this.reservaValidator = reservaValidator;
    }

    public Reserva inserirReserva(ReservaDTO dto) {
        Reserva reserva = montarEntidade(dto, null);
        // Se o status não foi informado, assume-se ATIVA automaticamente
        if (reserva.getStatus() == null) {
            reserva.setStatus(buscarStatusPorNomeEObrigatorio("ATIVA"));
        }
        reservaValidator.validar(reserva);
        return reservaRepository.save(reserva);
    }

    public Reserva atualizarReserva(Integer id, ReservaDTO dto) {
        if (!reservaRepository.existsById(id)) {
            throw new RegistroNaoEncontradoException("Não existe RESERVA com o ID informado.");
        }
        Reserva reserva = montarEntidade(dto, id);
        if (reserva.getStatus() == null) {
            reserva.setStatus(buscarStatusPorNomeEObrigatorio("ATIVA"));
        }
        reservaValidator.validar(reserva);
        return reservaRepository.save(reserva);
    }

    public Reserva buscarPorId(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Não existe RESERVA com o ID informado."));
    }

    public Reserva cancelarReserva(Integer id) {
        Reserva reserva = buscarPorId(id);
        reservaValidator.validarCancelamento(reserva);
        reserva.setStatus(buscarStatusPorNomeEObrigatorio("CANCELADA"));
        return reservaRepository.save(reserva);
    }

    // Filtros simples e combinados: recurso (código/nome), data ou período, horário, usuário, status
    public List<Reserva> pesquisar(Integer salaCodigo, Integer laboratorioCodigo, String recursoNome,
                                    LocalDate data, LocalDate dataInicio, LocalDate dataFim,
                                    LocalTime horaInicio, LocalTime horaFim,
                                    Integer usuarioId, String statusNome) {
        List<Reserva> resultado = reservaRepository.findAll();

        if (salaCodigo != null) {
            resultado = resultado.stream()
                    .filter(r -> r.getSala() != null && salaCodigo.equals(r.getSala().getCodigo()))
                    .toList();
        }
        if (laboratorioCodigo != null) {
            resultado = resultado.stream()
                    .filter(r -> r.getLaboratorio() != null && laboratorioCodigo.equals(r.getLaboratorio().getCodigo()))
                    .toList();
        }
        if (recursoNome != null && !recursoNome.isBlank()) {
            resultado = resultado.stream()
                    .filter(r -> (r.getSala() != null && r.getSala().getNome() != null
                            && r.getSala().getNome().toLowerCase().contains(recursoNome.toLowerCase()))
                            || (r.getLaboratorio() != null && r.getLaboratorio().getNome() != null
                            && r.getLaboratorio().getNome().toLowerCase().contains(recursoNome.toLowerCase())))
                    .toList();
        }
        if (data != null) {
            resultado = resultado.stream()
                    .filter(r -> data.isEqual(r.getDataInicial()))
                    .toList();
        }
        if (dataInicio != null) {
            resultado = resultado.stream()
                    .filter(r -> !r.getDataInicial().isBefore(dataInicio))
                    .toList();
        }
        if (dataFim != null) {
            resultado = resultado.stream()
                    .filter(r -> !r.getDataInicial().isAfter(dataFim))
                    .toList();
        }
        if (horaInicio != null) {
            resultado = resultado.stream()
                    .filter(r -> !r.getTempoInicial().isBefore(horaInicio))
                    .toList();
        }
        if (horaFim != null) {
            resultado = resultado.stream()
                    .filter(r -> !r.getTempoFinal().isAfter(horaFim))
                    .toList();
        }
        if (usuarioId != null) {
            resultado = resultado.stream()
                    .filter(r -> r.getUsuario() != null && usuarioId.equals(r.getUsuario().getId()))
                    .toList();
        }
        if (statusNome != null && !statusNome.isBlank()) {
            resultado = resultado.stream()
                    .filter(r -> r.getStatus() != null && statusNome.equalsIgnoreCase(r.getStatus().getNome()))
                    .toList();
        }
        return resultado;
    }

    // Job agendado: 1 minuto após o término do horário reservado, altera ATIVA -> CONCLUIDA
    public void concluirReservasVencidas() {
        LocalDateTime agora = LocalDateTime.now();
        Status statusConcluida = statusRepository.findByNomeIgnoreCaseAndTipo("CONCLUIDA", "RESERVA")
                .or(() -> statusRepository.findByNomeIgnoreCase("CONCLUÍDA"))
                .orElse(null);
        if (statusConcluida == null) {
            return; // Status CONCLUIDA ainda não cadastrado -> nada a fazer
        }

        List<Reserva> ativas = reservaRepository.findByStatus_NomeIgnoreCase("ATIVA");
        for (Reserva reserva : ativas) {
            LocalDateTime fimMaisUmMinuto = LocalDateTime.of(reserva.getDataFinal(), reserva.getTempoFinal()).plusMinutes(1);
            if (!agora.isBefore(fimMaisUmMinuto)) {
                reserva.setStatus(statusConcluida);
                reservaRepository.save(reserva);
            }
        }
    }

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    private Reserva montarEntidade(ReservaDTO dto, Integer id) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setDataInicial(converterData(dto.dataInicial()));
        reserva.setDataFinal(converterData(dto.dataFinal()));
        reserva.setTempoInicial(converterHora(dto.horaInicial()));
        reserva.setTempoFinal(converterHora(dto.horaFinal()));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new OperacaoNaoPermitidaException("O usuário informado (usuarioId) não existe."));
        reserva.setUsuario(usuario);

        if (dto.salaCodigo() != null) {
            Sala sala = salaRepository.findById(dto.salaCodigo())
                    .orElseThrow(() -> new OperacaoNaoPermitidaException("A sala informada (salaCodigo) não existe."));
            reserva.setSala(sala);
        }
        if (dto.laboratorioCodigo() != null) {
            Laboratorio laboratorio = laboratorioRepository.findById(dto.laboratorioCodigo())
                    .orElseThrow(() -> new OperacaoNaoPermitidaException("O laboratório informado (laboratorioCodigo) não existe."));
            reserva.setLaboratorio(laboratorio);
        }

        if (dto.statusId() != null) {
            Status status = statusRepository.findById(dto.statusId())
                    .orElseThrow(() -> new OperacaoNaoPermitidaException("O status informado (statusId) não existe."));
            reserva.setStatus(status);
        }

        return reserva;
    }

    private LocalDate converterData(String texto) {
        try {
            return LocalDate.parse(texto, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            throw new OperacaoNaoPermitidaException("Data Inválida");
        }
    }

    private LocalTime converterHora(String texto) {
        try {
            return LocalTime.parse(texto, FORMATO_HORA);
        } catch (DateTimeParseException e) {
            throw new OperacaoNaoPermitidaException("Hora Inválida");
        }
    }

    private Status buscarStatusPorNomeEObrigatorio(String nome) {
        Optional<Status> status = statusRepository.findByNomeIgnoreCaseAndTipo(nome, "RESERVA")
                .or(() -> statusRepository.findByNomeIgnoreCase(nome));
        return status.orElseThrow(() -> new OperacaoNaoPermitidaException(
                "É necessário cadastrar previamente o Status de Reserva '" + nome + "' (POST /status)."));
    }
}

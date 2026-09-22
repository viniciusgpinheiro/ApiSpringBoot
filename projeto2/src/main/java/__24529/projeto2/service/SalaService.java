package __24529.projeto2.service;

import __24529.projeto2.exceptions.OperacaoNaoPermitidaException;
import __24529.projeto2.exceptions.RegistroNaoEncontradoException;
import __24529.projeto2.model.Reserva;
import __24529.projeto2.model.Sala;
import __24529.projeto2.model.Status;
import __24529.projeto2.repository.ReservaRepository;
import __24529.projeto2.repository.SalaRepository;
import __24529.projeto2.repository.StatusRepository;
import __24529.projeto2.service.validator.SalaValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaValidator salaValidator;
    private final StatusRepository statusRepository;
    private final ReservaRepository reservaRepository;

    public SalaService(SalaRepository salaRepository,
                        SalaValidator salaValidator,
                        StatusRepository statusRepository,
                        ReservaRepository reservaRepository) {
        this.salaRepository = salaRepository;
        this.salaValidator = salaValidator;
        this.statusRepository = statusRepository;
        this.reservaRepository = reservaRepository;
    }

    public Sala inserirSala(Sala sala, Integer statusId) {
        sala.setStatus(resolverStatus(statusId));
        salaValidator.validar(sala);
        return salaRepository.save(sala);
    }

    public Sala atualizarSala(Integer codigo, Sala sala, Integer statusId) {
        if (!salaRepository.existsById(codigo)) {
            throw new RegistroNaoEncontradoException("Não existe SALA com o código informado.");
        }
        sala.setCodigo(codigo);
        sala.setStatus(resolverStatus(statusId));
        salaValidator.validar(sala);
        return salaRepository.save(sala);
    }

    public Sala buscarPorId(Integer codigo) {
        return salaRepository.findById(codigo)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Não existe SALA com o código informado."));
    }

    public List<Sala> pesquisar(String nome, Integer capacidade, String localizacao, String statusNome) {
        List<Sala> resultado = salaRepository.findAll();

        if (nome != null && !nome.isBlank()) {
            resultado = resultado.stream()
                    .filter(s -> s.getNome() != null && s.getNome().toLowerCase().contains(nome.toLowerCase()))
                    .toList();
        }
        if (capacidade != null) {
            resultado = resultado.stream()
                    .filter(s -> capacidade.equals(s.getCapacidade()))
                    .toList();
        }
        if (localizacao != null && !localizacao.isBlank()) {
            resultado = resultado.stream()
                    .filter(s -> s.getLocalizacao() != null && s.getLocalizacao().toLowerCase().contains(localizacao.toLowerCase()))
                    .toList();
        }
        if (statusNome != null && !statusNome.isBlank()) {
            resultado = resultado.stream()
                    .filter(s -> statusNome.equalsIgnoreCase(calcularStatusAtual(s)))
                    .toList();
        }
        return resultado;
    }

    // Calcula o status dinâmico do recurso: BLOQUEADO (manual) > OCUPADO (reserva ativa agora) > LIVRE
    public String calcularStatusAtual(Sala sala) {
        if (sala.getStatus() != null && "BLOQUEADO".equalsIgnoreCase(sala.getStatus().getNome())) {
            return "BLOQUEADO";
        }

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        boolean ocupada = reservaRepository.findBySala_CodigoAndDataInicial(sala.getCodigo(), hoje).stream()
                .filter(r -> r.getStatus() != null && "ATIVA".equalsIgnoreCase(r.getStatus().getNome()))
                .anyMatch(r -> !agora.isBefore(r.getTempoInicial()) && agora.isBefore(r.getTempoFinal()));

        return ocupada ? "OCUPADO" : "LIVRE";
    }

    private Status resolverStatus(Integer statusId) {
        if (statusId == null) {
            return null;
        }
        Optional<Status> status = statusRepository.findById(statusId);
        if (status.isEmpty()) {
            throw new OperacaoNaoPermitidaException("O status informado (statusId) não existe.");
        }
        return status.get();
    }
}

package __24529.projeto2.service;

import __24529.projeto2.exceptions.OperacaoNaoPermitidaException;
import __24529.projeto2.exceptions.RegistroNaoEncontradoException;
import __24529.projeto2.model.Laboratorio;
import __24529.projeto2.model.Status;
import __24529.projeto2.repository.LaboratorioRepository;
import __24529.projeto2.repository.ReservaRepository;
import __24529.projeto2.repository.StatusRepository;
import __24529.projeto2.service.validator.LaboratorioValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;
    private final LaboratorioValidator laboratorioValidator;
    private final StatusRepository statusRepository;
    private final ReservaRepository reservaRepository;

    public LaboratorioService(LaboratorioRepository laboratorioRepository,
                               LaboratorioValidator laboratorioValidator,
                               StatusRepository statusRepository,
                               ReservaRepository reservaRepository) {
        this.laboratorioRepository = laboratorioRepository;
        this.laboratorioValidator = laboratorioValidator;
        this.statusRepository = statusRepository;
        this.reservaRepository = reservaRepository;
    }

    public Laboratorio inserirLaboratorio(Laboratorio laboratorio, Integer statusId) {
        laboratorio.setStatus(resolverStatus(statusId));
        laboratorioValidator.validar(laboratorio);
        return laboratorioRepository.save(laboratorio);
    }

    public Laboratorio atualizarLaboratorio(Integer codigo, Laboratorio laboratorio, Integer statusId) {
        if (!laboratorioRepository.existsById(codigo)) {
            throw new RegistroNaoEncontradoException("Não existe LABORATÓRIO com o código informado.");
        }
        laboratorio.setCodigo(codigo);
        laboratorio.setStatus(resolverStatus(statusId));
        laboratorioValidator.validar(laboratorio);
        return laboratorioRepository.save(laboratorio);
    }

    public Laboratorio buscarPorId(Integer codigo) {
        return laboratorioRepository.findById(codigo)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Não existe LABORATÓRIO com o código informado."));
    }

    public List<Laboratorio> pesquisar(String nome, Integer capacidade, String localizacao, String statusNome) {
        List<Laboratorio> resultado = laboratorioRepository.findAll();

        if (nome != null && !nome.isBlank()) {
            resultado = resultado.stream()
                    .filter(l -> l.getNome() != null && l.getNome().toLowerCase().contains(nome.toLowerCase()))
                    .toList();
        }
        if (capacidade != null) {
            resultado = resultado.stream()
                    .filter(l -> capacidade.equals(l.getCapacidade()))
                    .toList();
        }
        if (localizacao != null && !localizacao.isBlank()) {
            resultado = resultado.stream()
                    .filter(l -> l.getLocalizacao() != null && l.getLocalizacao().toLowerCase().contains(localizacao.toLowerCase()))
                    .toList();
        }
        if (statusNome != null && !statusNome.isBlank()) {
            resultado = resultado.stream()
                    .filter(l -> statusNome.equalsIgnoreCase(calcularStatusAtual(l)))
                    .toList();
        }
        return resultado;
    }

    public String calcularStatusAtual(Laboratorio laboratorio) {
        if (laboratorio.getStatus() != null && "BLOQUEADO".equalsIgnoreCase(laboratorio.getStatus().getNome())) {
            return "BLOQUEADO";
        }

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        boolean ocupado = reservaRepository.findByLaboratorio_CodigoAndDataInicial(laboratorio.getCodigo(), hoje).stream()
                .filter(r -> r.getStatus() != null && "ATIVA".equalsIgnoreCase(r.getStatus().getNome()))
                .anyMatch(r -> !agora.isBefore(r.getTempoInicial()) && agora.isBefore(r.getTempoFinal()));

        return ocupado ? "OCUPADO" : "LIVRE";
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

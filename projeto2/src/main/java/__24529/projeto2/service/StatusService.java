package __24529.projeto2.service;

import __24529.projeto2.exceptions.RegistroNaoEncontradoException;
import __24529.projeto2.model.Status;
import __24529.projeto2.repository.StatusRepository;
import __24529.projeto2.service.validator.StatusValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {

    private final StatusRepository statusRepository;
    private final StatusValidator statusValidator;

    public StatusService(StatusRepository statusRepository, StatusValidator statusValidator) {
        this.statusRepository = statusRepository;
        this.statusValidator = statusValidator;
    }

    public Status inserirStatus(Status status) {
        statusValidator.validar(status);
        return statusRepository.save(status);
    }

    public Status atualizarStatus(Integer codigo, Status status) {
        if (!statusRepository.existsById(codigo)) {
            throw new RegistroNaoEncontradoException("Não existe STATUS com o código informado.");
        }
        status.setCodigo(codigo);
        statusValidator.validar(status);
        return statusRepository.save(status);
    }

    public Status buscarPorId(Integer codigo) {
        return statusRepository.findById(codigo)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Não existe STATUS com o código informado."));
    }

    public List<Status> pesquisarPorTipoENome(String tipo, String nome) {
        if (tipo != null && !tipo.isBlank() && nome != null && !nome.isBlank()) {
            return statusRepository.findByTipoAndNomeContainingIgnoreCase(tipo, nome);
        }
        if (tipo != null && !tipo.isBlank()) {
            return statusRepository.findByTipo(tipo);
        }
        if (nome != null && !nome.isBlank()) {
            return statusRepository.findByNomeContainingIgnoreCase(nome);
        }
        return statusRepository.findAll();
    }

    public List<Status> listarStatusDeRecurso() {
        return statusRepository.findByTipo("RECURSO");
    }

    public List<Status> listarStatusDeReserva() {
        return statusRepository.findByTipo("RESERVA");
    }
}

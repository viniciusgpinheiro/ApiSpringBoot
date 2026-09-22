package __24529.projeto2.service.validator;

import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Status;
import __24529.projeto2.repository.StatusRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StatusValidator {

    private final StatusRepository statusRepository;

    public StatusValidator(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public void validar(Status status) {
        if (existeNomeETipoCadastrados(status)) {
            throw new RegistroDuplicadoException(
                    "Já existe um status cadastrado com este nome para este tipo.");
        }
    }

    private boolean existeNomeETipoCadastrados(Status status) {
        Optional<Status> encontrado = statusRepository.findByNomeIgnoreCaseAndTipo(status.getNome(), status.getTipo());

        if (status.getCodigo() == null) {
            return encontrado.isPresent();
        }
        return encontrado.isPresent() && !status.getCodigo().equals(encontrado.get().getCodigo());
    }
}

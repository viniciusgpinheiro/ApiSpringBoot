package __24529.projeto2.service.validator;

import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Laboratorio;
import __24529.projeto2.repository.LaboratorioRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LaboratorioValidator {

    private final LaboratorioRepository laboratorioRepository;

    public LaboratorioValidator(LaboratorioRepository laboratorioRepository) {
        this.laboratorioRepository = laboratorioRepository;
    }

    public void validar(Laboratorio laboratorio) {
        if (existeNomeELocalizacaoCadastrados(laboratorio)) {
            throw new RegistroDuplicadoException(
                    "Já existe um laboratório cadastrado com este nome nesta localização.");
        }
    }

    private boolean existeNomeELocalizacaoCadastrados(Laboratorio laboratorio) {
        List<Laboratorio> encontrados = laboratorioRepository.findByNomeContainingIgnoreCase(laboratorio.getNome())
                .stream()
                .filter(l -> l.getLocalizacao().equalsIgnoreCase(laboratorio.getLocalizacao()))
                .toList();

        if (laboratorio.getCodigo() == null) {
            return !encontrados.isEmpty();
        }
        return encontrados.stream().anyMatch(l -> !l.getCodigo().equals(laboratorio.getCodigo()));
    }
}

package __24529.projeto2.service.validator;

import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Sala;
import __24529.projeto2.repository.SalaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SalaValidator {

    private final SalaRepository salaRepository;

    public SalaValidator(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    public void validar(Sala sala) {
        if (existeNomeELocalizacaoCadastrados(sala)) {
            throw new RegistroDuplicadoException(
                    "Já existe uma sala cadastrada com este nome nesta localização.");
        }
    }

    private boolean existeNomeELocalizacaoCadastrados(Sala sala) {
        List<Sala> encontradas = salaRepository.findByNomeContainingIgnoreCase(sala.getNome())
                .stream()
                .filter(s -> s.getLocalizacao().equalsIgnoreCase(sala.getLocalizacao()))
                .toList();

        if (sala.getCodigo() == null) {
            return !encontradas.isEmpty();
        }
        return encontradas.stream().anyMatch(s -> !s.getCodigo().equals(sala.getCodigo()));
    }
}

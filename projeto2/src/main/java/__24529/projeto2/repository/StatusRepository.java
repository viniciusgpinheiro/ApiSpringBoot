package __24529.projeto2.repository;

import __24529.projeto2.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Integer> {

    List<Status> findByTipo(String tipo);

    Optional<Status> findByNomeIgnoreCase(String nome);

    Optional<Status> findByNomeIgnoreCaseAndTipo(String nome, String tipo);

    List<Status> findByTipoAndNomeContainingIgnoreCase(String tipo, String nome);

    List<Status> findByNomeContainingIgnoreCase(String nome);
}

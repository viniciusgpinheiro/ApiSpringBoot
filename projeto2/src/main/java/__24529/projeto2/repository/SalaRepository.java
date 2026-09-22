package __24529.projeto2.repository;


import __24529.projeto2.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaRepository extends JpaRepository<Sala, Integer> {

    List<Sala> findByNomeContainingIgnoreCase(String nome);

    List<Sala> findByCapacidade(Integer capacidade);

    List<Sala> findByLocalizacaoContainingIgnoreCase(String localizacao);

    List<Sala> findByStatus_NomeIgnoreCase(String statusNome);
}

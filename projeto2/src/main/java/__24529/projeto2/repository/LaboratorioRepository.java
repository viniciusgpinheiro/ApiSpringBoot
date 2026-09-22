package __24529.projeto2.repository;


import __24529.projeto2.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> {

    List<Laboratorio> findByNomeContainingIgnoreCase(String nome);

    List<Laboratorio> findByCapacidade(Integer capacidade);

    List<Laboratorio> findByLocalizacaoContainingIgnoreCase(String localizacao);

    List<Laboratorio> findByStatus_NomeIgnoreCase(String statusNome);
}

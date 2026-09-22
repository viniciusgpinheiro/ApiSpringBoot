package __24529.projeto2.repository;


import __24529.projeto2.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    List<Reserva> findBySala_Codigo(Integer salaCodigo);

    List<Reserva> findByLaboratorio_Codigo(Integer laboratorioCodigo);

    List<Reserva> findBySala_CodigoAndDataInicial(Integer salaCodigo, LocalDate dataInicial);

    List<Reserva> findByLaboratorio_CodigoAndDataInicial(Integer laboratorioCodigo, LocalDate dataInicial);

    List<Reserva> findByUsuario_Id(Integer usuarioId);

    List<Reserva> findByStatus_NomeIgnoreCase(String statusNome);

    List<Reserva> findByDataInicial(LocalDate dataInicial);

    List<Reserva> findByDataInicialBetween(LocalDate dataInicio, LocalDate dataFim);
}

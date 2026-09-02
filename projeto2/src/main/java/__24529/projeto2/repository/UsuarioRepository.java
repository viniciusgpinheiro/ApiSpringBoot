package __24529.projeto2.repository;


import __24529.projeto2.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCpf(String cpf);

    List<Usuario> findByEmail(String email);

    List<Usuario> findByEmailAndDataAniversario(String email, LocalDate dataAniversario);

    List<Usuario> findByDataAniversario(LocalDate dataAniversario);
}

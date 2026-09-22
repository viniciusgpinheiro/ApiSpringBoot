package __24529.projeto2.service.validator;

import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Usuario;
import __24529.projeto2.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioValidatorTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioValidator usuarioValidator;

    @BeforeEach
    void setUp() {
        usuarioValidator = new UsuarioValidator(usuarioRepository);
    }

    private Usuario novoUsuario(Integer id, String cpf, String email) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setCpf(cpf);
        usuario.setNome("Fulano de Tal Completo");
        usuario.setDataAniversario(LocalDate.of(2000, 1, 1));
        usuario.setCelular("19999998888");
        usuario.setEmail(email);
        usuario.setSenha("senha123");
        return usuario;
    }

    @Test
    void deveRejeitarCpfJaCadastradoNaInclusao() {
        Usuario novo = novoUsuario(null, "52998224725", "novo@teste.com.br");
        Usuario existente = novoUsuario(1, "52998224725", "outro@teste.com.br");

        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(existente));

        assertThrows(RegistroDuplicadoException.class, () -> usuarioValidator.validar(novo));
    }

    @Test
    void deveRejeitarEmailJaCadastradoNaInclusao() {
        Usuario novo = novoUsuario(null, "52998224725", "duplicado@teste.com.br");
        Usuario existente = novoUsuario(1, "11144477735", "duplicado@teste.com.br");

        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("duplicado@teste.com.br")).thenReturn(List.of(existente));

        assertThrows(RegistroDuplicadoException.class, () -> usuarioValidator.validar(novo));
    }

    @Test
    void devePermitirAtualizarOProprioRegistroComMesmoCpfEEmail() {
        Usuario atualizado = novoUsuario(1, "52998224725", "mesmo@teste.com.br");
        Usuario existente = novoUsuario(1, "52998224725", "mesmo@teste.com.br");

        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByEmail("mesmo@teste.com.br")).thenReturn(List.of(existente));

        assertDoesNotThrow(() -> usuarioValidator.validar(atualizado));
    }

    @Test
    void deveRejeitarAtualizacaoComCpfDeOutroUsuario() {
        Usuario atualizado = novoUsuario(2, "52998224725", "usuario2@teste.com.br");
        Usuario donoDoCpf = novoUsuario(1, "52998224725", "usuario1@teste.com.br");

        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(donoDoCpf));

        assertThrows(RegistroDuplicadoException.class, () -> usuarioValidator.validar(atualizado));
    }

    @Test
    void devePermitirCadastroSemConflitos() {
        Usuario novo = novoUsuario(null, "52998224725", "livre@teste.com.br");

        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("livre@teste.com.br")).thenReturn(List.of());

        assertDoesNotThrow(() -> usuarioValidator.validar(novo));
    }
}

package __24529.projeto2.service;

import __24529.projeto2.exceptions.OperacaoNaoPermitidaException;
import __24529.projeto2.model.Usuario;
import __24529.projeto2.repository.ReservaRepository;
import __24529.projeto2.repository.UsuarioRepository;
import __24529.projeto2.service.validator.UsuarioValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioValidator usuarioValidator;
    private final ReservaRepository reservaRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioValidator usuarioValidator,
            ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioValidator = usuarioValidator;
        this.reservaRepository = reservaRepository;
    }

    public Usuario inserirUsuario(Usuario usuario) {
        // Validar os campos que vieram do JSON antes de incluir no BD
        usuarioValidator.validar(usuario);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> pegarDadosUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public void excluirUsuarioPorId(Integer id) {
        if (possuiReserva(id)) {
            throw new OperacaoNaoPermitidaException(
                    "Não é permitido excluir um Usuário que " +
                            "possui 1 ou mais reservas associadas!");
        }
        usuarioRepository.deleteById(id);
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            throw new IllegalArgumentException(
                    "Não existe o USUÁRIO com o ID informado.");
        }
        // Validar os campos que vieram do JSON antes de atualizar no BD
        usuarioValidator.validar(usuario);
        return usuarioRepository.save(usuario);
    }

    // Consulta do escopo: dados dos usuários cadastrados por e-mail e data de aniversário
    public List<Usuario> pesquisarPorEmailEDataAniversario(String email, LocalDate dataAniversario) {
        if (email != null && dataAniversario != null) {
            return usuarioRepository.findByEmailAndDataAniversario(email, dataAniversario);
        }

        if (email != null) {
            return usuarioRepository.findByEmail(email);
        }

        if (dataAniversario != null) {
            return usuarioRepository.findByDataAniversario(dataAniversario);
        }

        return usuarioRepository.findAll();
    }

    // Método de Login solicitado no escopo do projeto
    public boolean realizarLogin(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email).stream().findFirst();
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return usuario.getSenha().equals(senha);
        }
        return false;
    }

    public boolean possuiReserva(Integer idUsuario) {
        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
        return usuario.map(reservaRepository::existsByUsuario).orElse(false);
    }
}
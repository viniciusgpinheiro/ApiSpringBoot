package __24529.projeto2.service.validator;

import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Usuario;
import __24529.projeto2.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioValidator {

    private final UsuarioRepository usuarioRepository;

    public UsuarioValidator(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void validar(Usuario usuario) {
        // Verificar se já existe usuário cadastrado com o mesmo CPF
        if (existeCpfCadastrado(usuario)) {
            throw new RegistroDuplicadoException(
                    "Já existe um usuário cadastrado com este CPF.");
        }

        // Verificar se já existe usuário cadastrado com o mesmo E-mail
        if (existeEmailCadastrado(usuario)) {
            throw new RegistroDuplicadoException(
                    "Já existe um usuário cadastrado com este E-mail.");
        }
    }

    private boolean existeCpfCadastrado(Usuario usuario) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCpf(usuario.getCpf());

        // Verificando se é uma inclusão de novo registro
        if (usuario.getId() == null) {
            return usuarioEncontrado.isPresent();
        }

        // Verificando se é uma atualização de registro existente
        return usuarioEncontrado.isPresent() &&
                !usuario.getId().equals(usuarioEncontrado.get().getId());
    }

    private boolean existeEmailCadastrado(Usuario usuario) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail(usuario.getEmail())
                .stream()
                .findFirst();

        // Verificando se é uma inclusão de novo registro
        if (usuario.getId() == null) {
            return usuarioEncontrado.isPresent();
        }

        // Verificando se é uma atualização de registro existente
        return usuarioEncontrado.isPresent() &&
                !usuario.getId().equals(usuarioEncontrado.get().getId());
    }
}
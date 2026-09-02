package __24529.projeto2.controler;

import __24529.projeto2.controler.dto.ErroResposta;
import __24529.projeto2.controler.dto.UsuarioDTO;
import __24529.projeto2.exceptions.RegistroDuplicadoException;
import __24529.projeto2.model.Usuario;
import __24529.projeto2.service.UsuarioService;
import __24529.projeto2.service.validator.UsuarioValidator;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/usuario")
// http://localhost:8080/usuario
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioValidator usuarioValidator;

    public UsuarioController(UsuarioService usuarioService,
                             UsuarioValidator usuarioValidator) {
        this.usuarioService = usuarioService;
        this.usuarioValidator = usuarioValidator;
    }

    @PostMapping
    public ResponseEntity<Object> incluirUsuario(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        try {
            Usuario usuarioEntidade = usuarioDTO.mapearDadosParaEntidadeUsuario();
            usuarioService.inserirUsuario(usuarioEntidade);

            // http://localhost:8080/usuario/idDoNovoRegistro
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(usuarioEntidade.getId())
                    .toUri();

            System.out.println("Dados do novo USUARIO inseridos com sucesso! \nDados vindo do JSON = " + usuarioDTO);
            return new ResponseEntity("Dados da entidade USUARIO inseridos com sucesso!" + usuarioDTO, HttpStatus.CREATED);
            //return ResponseEntity.created(location).build();
        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }


    @GetMapping("{id}")
    public ResponseEntity<UsuarioDTO> pegarDadosUsuario(@PathVariable("id") Integer id) {
        Optional<Usuario> usuarioOptional = usuarioService.pegarDadosUsuarioPorId(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            UsuarioDTO usuarioDTO = new UsuarioDTO(
                    usuario.getId(),
                    usuario.getCpf(),
                    usuario.getNome(),
                    usuario.getDataAniversario(),
                    usuario.getCelular(),
                    usuario.getEmail(),
                    usuario.getSenha()
            );
            return ResponseEntity.ok(usuarioDTO);
        }
        return ResponseEntity.notFound().build();
    }
}

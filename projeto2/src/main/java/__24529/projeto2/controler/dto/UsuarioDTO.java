package __24529.projeto2.controler.dto;

import __24529.projeto2.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioDTO(
        Integer id,

        @NotBlank(message = "Campo Obrigatório")
        String cpf,

        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 3, max = 150, message = "Campo fora do tamanho permitido")
        String nome,

        @NotNull(message = "Campo Obrigatório")
        LocalDate dataAniversario,

        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 10, max = 20, message = "Campo fora do tamanho permitido")
        String celular,

        @NotBlank(message = "Campo Obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 100, message = "Campo fora do tamanho permitido")
        String email,

        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 6, max = 255, message = "A senha deve ter entre 6 e 255 caracteres")
        String senha
) {

    public Usuario mapearDadosParaEntidadeUsuario() {
        Usuario usuario = new Usuario();
        usuario.setCpf(this.cpf);
        usuario.setNome(this.nome);
        usuario.setDataAniversario(this.dataAniversario);
        usuario.setCelular(this.celular);
        usuario.setEmail(this.email);
        usuario.setSenha(this.senha);
        return usuario;
    }
}

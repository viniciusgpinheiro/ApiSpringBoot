package __24529.projeto2.controler.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(
        @NotBlank(message = "Campo obrigatório")
        String email,

        @NotBlank(message = "Campo obrigatório")
        String senha
) {
}

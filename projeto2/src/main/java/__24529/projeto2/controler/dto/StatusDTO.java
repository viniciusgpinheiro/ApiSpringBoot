package __24529.projeto2.controler.dto;

import __24529.projeto2.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StatusDTO(
        Integer codigo,

        @NotBlank(message = "Campo obrigatório")
        @Size(min = 15, max = 20, message = "Quantidade de caracteres incorreta!")
        String nome,

        // "RECURSO" -> status de Laboratório/Sala (LIVRE, OCUPADO, BLOQUEADO)
        // "RESERVA" -> status de Reserva (ATIVA, CANCELADA, CONCLUIDA)
        @NotBlank(message = "Campo obrigatório")
        @Pattern(regexp = "RECURSO|RESERVA", message = "O tipo deve ser RECURSO ou RESERVA")
        String tipo
) {
    public Status mapearParaEntidade() {
        Status status = new Status();
        status.setCodigo(this.codigo);
        status.setNome(this.nome);
        status.setTipo(this.tipo);
        return status;
    }
}

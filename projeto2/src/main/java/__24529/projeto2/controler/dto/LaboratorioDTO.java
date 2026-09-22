package __24529.projeto2.controler.dto;

import __24529.projeto2.model.Laboratorio;
import __24529.projeto2.model.Status;
import __24529.projeto2.validation.CpfValido;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LaboratorioDTO(
        Integer codigo,

        // Literal do enunciado: Nome not null, 11 caracteres e validação de CPF válido.
        @NotBlank(message = "Campo obrigatório")
        @Size(min = 11, max = 11, message = "Quantidade de caracteres incorreta!")
        @CpfValido(message = "CPF inválido")
        String nome,

        @NotNull(message = "Campo obrigatório")
        @Min(value = 1, message = "Valor fora do escopo")
        @Max(value = 40, message = "Valor fora do escopo")
        Integer capacidade,

        @NotBlank(message = "Campo obrigatório")
        @Size(min = 15, max = 50, message = "Quantidade de caracteres incorreta!")
        String localizacao,

        // Opcional: id de um Status (tipo RECURSO) para marcar o recurso, ex: BLOQUEADO
        Integer statusId,

        // Somente para resposta (GET): status calculado (LIVRE / OCUPADO / BLOQUEADO)
        String statusAtual
) {
    public Laboratorio mapearParaEntidade(Status status) {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setCodigo(this.codigo);
        laboratorio.setNome(this.nome);
        laboratorio.setCapacidade(this.capacidade);
        laboratorio.setLocalizacao(this.localizacao);
        laboratorio.setStatus(status);
        return laboratorio;
    }
}

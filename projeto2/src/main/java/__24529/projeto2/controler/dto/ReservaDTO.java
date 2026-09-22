package __24529.projeto2.controler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ReservaDTO(
        Integer id,

        // Formato exigido no enunciado: DD/MM/AAAA
        @NotBlank(message = "Campo obrigatório")
        @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Data Inválida")
        String dataInicial,

        @NotBlank(message = "Campo obrigatório")
        @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Data Inválida")
        String dataFinal,

        // Formato exigido no enunciado: HH:MM:SS
        @NotBlank(message = "Campo obrigatório")
        @Pattern(regexp = "\\d{2}:\\d{2}:\\d{2}", message = "Hora Inválida")
        String horaInicial,

        @NotBlank(message = "Campo obrigatório")
        @Pattern(regexp = "\\d{2}:\\d{2}:\\d{2}", message = "Hora Inválida")
        String horaFinal,

        @NotNull(message = "Campo obrigatório")
        Integer usuarioId,

        // Informe APENAS um dos dois: salaCodigo OU laboratorioCodigo
        Integer salaCodigo,
        Integer laboratorioCodigo,

        // Opcional no POST (assume-se "ATIVA" automaticamente quando não informado)
        Integer statusId
) {
}

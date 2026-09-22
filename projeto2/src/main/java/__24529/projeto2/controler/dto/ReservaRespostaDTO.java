package __24529.projeto2.controler.dto;

public record ReservaRespostaDTO(
        Integer id,
        String dataInicial,
        String dataFinal,
        String horaInicial,
        String horaFinal,
        Integer usuarioId,
        String usuarioNome,
        Integer salaCodigo,
        Integer laboratorioCodigo,
        String recursoNome,
        Integer statusId,
        String statusNome
) {
}

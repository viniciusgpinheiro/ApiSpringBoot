package __24529.projeto2.controler;

import __24529.projeto2.controler.dto.ReservaDTO;
import __24529.projeto2.controler.dto.ReservaRespostaDTO;
import __24529.projeto2.model.Reserva;
import __24529.projeto2.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/reserva")
// http://localhost:8080/reserva
public class ReservaController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaRespostaDTO> incluirReserva(@RequestBody @Valid ReservaDTO reservaDTO) {
        Reserva reserva = reservaService.inserirReserva(reservaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDTO(reserva));
    }

    @PutMapping("{id}")
    public ResponseEntity<ReservaRespostaDTO> atualizarReserva(@PathVariable("id") Integer id,
                                                                @RequestBody @Valid ReservaDTO reservaDTO) {
        Reserva reserva = reservaService.atualizarReserva(id, reservaDTO);
        return ResponseEntity.ok(paraDTO(reserva));
    }

    @GetMapping("{id}")
    public ResponseEntity<ReservaRespostaDTO> buscarPorId(@PathVariable("id") Integer id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return ResponseEntity.ok(paraDTO(reserva));
    }

    // Cancelamento de reserva: permitido apenas até 24h antes da data/hora reservada
    @PatchMapping("{id}/cancelar")
    public ResponseEntity<ReservaRespostaDTO> cancelarReserva(@PathVariable("id") Integer id) {
        Reserva reserva = reservaService.cancelarReserva(id);
        return ResponseEntity.ok(paraDTO(reserva));
    }

    // Filtros simples e combinados: código/nome do recurso, data ou período, horário, usuário e status
    @GetMapping
    public ResponseEntity<List<ReservaRespostaDTO>> pesquisar(
            @RequestParam(required = false) Integer salaCodigo,
            @RequestParam(required = false) Integer laboratorioCodigo,
            @RequestParam(required = false) String recursoNome,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate data,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horaInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm:ss") LocalTime horaFim,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) String status) {
        List<Reserva> reservas = reservaService.pesquisar(salaCodigo, laboratorioCodigo, recursoNome,
                data, dataInicio, dataFim, horaInicio, horaFim, usuarioId, status);
        List<ReservaRespostaDTO> resultado = reservas.stream().map(this::paraDTO).toList();
        return ResponseEntity.ok(resultado);
    }

    private ReservaRespostaDTO paraDTO(Reserva reserva) {
        boolean temSala = reserva.getSala() != null;
        return new ReservaRespostaDTO(
                reserva.getId(),
                reserva.getDataInicial().format(FORMATO_DATA),
                reserva.getDataFinal().format(FORMATO_DATA),
                reserva.getTempoInicial().format(FORMATO_HORA),
                reserva.getTempoFinal().format(FORMATO_HORA),
                reserva.getUsuario() != null ? reserva.getUsuario().getId() : null,
                reserva.getUsuario() != null ? reserva.getUsuario().getNome() : null,
                temSala ? reserva.getSala().getCodigo() : null,
                !temSala && reserva.getLaboratorio() != null ? reserva.getLaboratorio().getCodigo() : null,
                temSala ? reserva.getSala().getNome()
                        : (reserva.getLaboratorio() != null ? reserva.getLaboratorio().getNome() : null),
                reserva.getStatus() != null ? reserva.getStatus().getCodigo() : null,
                reserva.getStatus() != null ? reserva.getStatus().getNome() : null
        );
    }
}

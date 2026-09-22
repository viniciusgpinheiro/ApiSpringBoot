package __24529.projeto2.controler;

import __24529.projeto2.controler.dto.StatusDTO;
import __24529.projeto2.controler.mapper.StatusMapper;
import __24529.projeto2.model.Status;
import __24529.projeto2.service.StatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/status")
// http://localhost:8080/status
public class StatusController {

    private final StatusService statusService;
    private final StatusMapper statusMapper;

    public StatusController(StatusService statusService, StatusMapper statusMapper) {
        this.statusService = statusService;
        this.statusMapper = statusMapper;
    }

    @PostMapping
    public ResponseEntity<StatusDTO> incluirStatus(@RequestBody @Valid StatusDTO statusDTO) {
        Status statusEntidade = statusDTO.mapearParaEntidade();
        Status statusSalvo = statusService.inserirStatus(statusEntidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(statusMapper.toDTO(statusSalvo));
    }

    @PutMapping("{codigo}")
    public ResponseEntity<StatusDTO> atualizarStatus(@PathVariable("codigo") Integer codigo,
                                                       @RequestBody @Valid StatusDTO statusDTO) {
        Status statusEntidade = statusDTO.mapearParaEntidade();
        Status statusAtualizado = statusService.atualizarStatus(codigo, statusEntidade);
        return ResponseEntity.ok(statusMapper.toDTO(statusAtualizado));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<StatusDTO> buscarPorCodigo(@PathVariable("codigo") Integer codigo) {
        Status status = statusService.buscarPorId(codigo);
        return ResponseEntity.ok(statusMapper.toDTO(status));
    }

    // Consulta dos status de RECURSO cadastrados (LIVRE / OCUPADO / BLOQUEADO)
    @GetMapping("/recurso")
    public ResponseEntity<List<StatusDTO>> listarStatusDeRecurso() {
        List<StatusDTO> resultado = statusService.listarStatusDeRecurso().stream().map(statusMapper::toDTO).toList();
        return ResponseEntity.ok(resultado);
    }

    // Consulta dos status de RESERVA cadastrados (ATIVA / CANCELADA / CONCLUIDA)
    @GetMapping("/reserva")
    public ResponseEntity<List<StatusDTO>> listarStatusDeReserva() {
        List<StatusDTO> resultado = statusService.listarStatusDeReserva().stream().map(statusMapper::toDTO).toList();
        return ResponseEntity.ok(resultado);
    }

    // Filtros isolados ou combinados por tipo e nome
    @GetMapping
    public ResponseEntity<List<StatusDTO>> pesquisar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String nome) {
        List<StatusDTO> resultado = statusService.pesquisarPorTipoENome(tipo, nome).stream().map(statusMapper::toDTO).toList();
        return ResponseEntity.ok(resultado);
    }
}

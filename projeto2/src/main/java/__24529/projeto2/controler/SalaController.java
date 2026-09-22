package __24529.projeto2.controler;

import __24529.projeto2.controler.dto.SalaDTO;
import __24529.projeto2.controler.mapper.SalaMapper;
import __24529.projeto2.model.Sala;
import __24529.projeto2.model.Status;
import __24529.projeto2.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sala")
// http://localhost:8080/sala
public class SalaController {

    private final SalaService salaService;
    private final SalaMapper salaMapper;

    public SalaController(SalaService salaService, SalaMapper salaMapper) {
        this.salaService = salaService;
        this.salaMapper = salaMapper;
    }

    @PostMapping
    public ResponseEntity<SalaDTO> incluirSala(@RequestBody @Valid SalaDTO salaDTO) {
        Sala salaEntidade = salaDTO.mapearParaEntidade(null);
        Sala salaSalva = salaService.inserirSala(salaEntidade, salaDTO.statusId());
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDTOComStatus(salaSalva));
    }

    @PutMapping("{codigo}")
    public ResponseEntity<SalaDTO> atualizarSala(@PathVariable("codigo") Integer codigo,
                                                  @RequestBody @Valid SalaDTO salaDTO) {
        Sala salaEntidade = salaDTO.mapearParaEntidade(null);
        Sala salaAtualizada = salaService.atualizarSala(codigo, salaEntidade, salaDTO.statusId());
        return ResponseEntity.ok(paraDTOComStatus(salaAtualizada));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<SalaDTO> buscarPorCodigo(@PathVariable("codigo") Integer codigo) {
        Sala sala = salaService.buscarPorId(codigo);
        return ResponseEntity.ok(paraDTOComStatus(sala));
    }

    // Filtros isolados ou combinados por nome, capacidade, localização e status
    @GetMapping
    public ResponseEntity<List<SalaDTO>> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer capacidade,
            @RequestParam(required = false) String localizacao,
            @RequestParam(required = false) String status) {
        List<Sala> salas = salaService.pesquisar(nome, capacidade, localizacao, status);
        List<SalaDTO> resultado = salas.stream().map(this::paraDTOComStatus).toList();
        return ResponseEntity.ok(resultado);
    }

    private SalaDTO paraDTOComStatus(Sala sala) {
        SalaDTO base = salaMapper.toDTO(sala);
        String statusAtual = salaService.calcularStatusAtual(sala);
        return new SalaDTO(base.codigo(), base.nome(), base.capacidade(), base.localizacao(),
                base.statusId(), statusAtual);
    }
}

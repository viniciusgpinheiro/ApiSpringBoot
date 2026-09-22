package __24529.projeto2.controler;

import __24529.projeto2.controler.dto.LaboratorioDTO;
import __24529.projeto2.controler.mapper.LaboratorioMapper;
import __24529.projeto2.model.Laboratorio;
import __24529.projeto2.service.LaboratorioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/laboratorio")
// http://localhost:8080/laboratorio
public class LaboratorioController {

    private final LaboratorioService laboratorioService;
    private final LaboratorioMapper laboratorioMapper;

    public LaboratorioController(LaboratorioService laboratorioService, LaboratorioMapper laboratorioMapper) {
        this.laboratorioService = laboratorioService;
        this.laboratorioMapper = laboratorioMapper;
    }

    @PostMapping
    public ResponseEntity<LaboratorioDTO> incluirLaboratorio(@RequestBody @Valid LaboratorioDTO laboratorioDTO) {
        Laboratorio laboratorioEntidade = laboratorioDTO.mapearParaEntidade(null);
        Laboratorio laboratorioSalvo = laboratorioService.inserirLaboratorio(laboratorioEntidade, laboratorioDTO.statusId());
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDTOComStatus(laboratorioSalvo));
    }

    @PutMapping("{codigo}")
    public ResponseEntity<LaboratorioDTO> atualizarLaboratorio(@PathVariable("codigo") Integer codigo,
                                                                @RequestBody @Valid LaboratorioDTO laboratorioDTO) {
        Laboratorio laboratorioEntidade = laboratorioDTO.mapearParaEntidade(null);
        Laboratorio laboratorioAtualizado = laboratorioService.atualizarLaboratorio(codigo, laboratorioEntidade, laboratorioDTO.statusId());
        return ResponseEntity.ok(paraDTOComStatus(laboratorioAtualizado));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<LaboratorioDTO> buscarPorCodigo(@PathVariable("codigo") Integer codigo) {
        Laboratorio laboratorio = laboratorioService.buscarPorId(codigo);
        return ResponseEntity.ok(paraDTOComStatus(laboratorio));
    }

    // Filtros isolados ou combinados por nome, capacidade, localização e status
    @GetMapping
    public ResponseEntity<List<LaboratorioDTO>> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer capacidade,
            @RequestParam(required = false) String localizacao,
            @RequestParam(required = false) String status) {
        List<Laboratorio> laboratorios = laboratorioService.pesquisar(nome, capacidade, localizacao, status);
        List<LaboratorioDTO> resultado = laboratorios.stream().map(this::paraDTOComStatus).toList();
        return ResponseEntity.ok(resultado);
    }

    private LaboratorioDTO paraDTOComStatus(Laboratorio laboratorio) {
        LaboratorioDTO base = laboratorioMapper.toDTO(laboratorio);
        String statusAtual = laboratorioService.calcularStatusAtual(laboratorio);
        return new LaboratorioDTO(base.codigo(), base.nome(), base.capacidade(), base.localizacao(),
                base.statusId(), statusAtual);
    }
}

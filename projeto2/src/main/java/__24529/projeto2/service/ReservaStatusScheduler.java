package __24529.projeto2.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservaStatusScheduler {

    private final ReservaService reservaService;

    public ReservaStatusScheduler(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Scheduled(fixedRate = 60000)
    public void atualizarReservasVencidas() {
        reservaService.concluirReservasVencidas();
    }
}

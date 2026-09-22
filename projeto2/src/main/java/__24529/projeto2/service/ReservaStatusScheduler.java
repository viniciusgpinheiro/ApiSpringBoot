package __24529.projeto2.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservaStatusScheduler {

    private final ReservaService reservaService;

    public ReservaStatusScheduler(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // Roda a cada 1 minuto: reservas ATIVAS cujo horário final + 1 minuto já passou
    // são automaticamente alteradas para CONCLUIDA.
    @Scheduled(fixedRate = 60000)
    public void atualizarReservasVencidas() {
        reservaService.concluirReservasVencidas();
    }
}

package com.hubsaude.assinador.infrastructure;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Gerencia o desligamento automático do servidor com base em um período de inatividade.
 *
 * <p>O comportamento é controlado pela variável de ambiente {@code HUBSAUDE_TIMEOUT_MINUTES}. 
 * Se ela estiver ausente ou configurada como zero, o mecanismo de monitoramento (watchdog) 
 * permanece desativado. Caso contrário, uma thread dedicada verifica a cada 30 segundos se o 
 * tempo decorrido desde a última requisição excedeu o limite definido, encerrando a 
 * aplicação via {@code System.exit(0)} quando necessário.
 *
 * <p>Ao iniciar o servidor por linha de comando, o CLI injeta essa variável através do 
 * argumento {@code assinatura start --timeout N}.
 */
@Component
	public class InactivityShutdown implements ApplicationRunner {

    private final RequestTimestamp requestTimestamp;

    public InactivityShutdown(RequestTimestamp requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }

    @Override
    public void run(ApplicationArguments args) {
        String env = System.getenv("HUBSAUDE_TIMEOUT_MINUTES");
        if (env == null || env.isBlank()) return;

        int minutes;
        try {
            minutes = Integer.parseInt(env.trim());
            if (minutes <= 0) return;
        } catch (NumberFormatException e) {
            return;
        }

        long timeoutMs = (long) minutes * 60_000L;
        Thread watchdog = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                if (System.currentTimeMillis() - requestTimestamp.get() > timeoutMs) {
                    System.err.printf("Servidor encerrando por inatividade (timeout=%dmin)%n", minutes);
                    System.exit(0);
                }
            }
        });
        watchdog.setDaemon(true);
        watchdog.setName("inactivity-watchdog");
        watchdog.start();
    }
}

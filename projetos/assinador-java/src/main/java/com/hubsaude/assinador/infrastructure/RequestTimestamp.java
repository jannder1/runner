package com.hubsaude.assinador.infrastructure;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Armazena a data e hora exata da requisição HTTP mais recente processada pelo servidor.
 */
@Component
public class RequestTimestamp {

    private final AtomicLong lastRequest = new AtomicLong(System.currentTimeMillis());

    public void touch() {
        lastRequest.set(System.currentTimeMillis());
    }

    public long get() {
        return lastRequest.get();
    }
}

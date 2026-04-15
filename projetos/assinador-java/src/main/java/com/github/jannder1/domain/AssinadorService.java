package com.github.jannder1.domain;

/**
 * Representa o Componente de Assinatura no modelo C4.
 */
public interface AssinadorService {
    byte[] assinar(byte[] documento, String chaveId);
    boolean validar(byte[] documento, byte[] assinatura);
}

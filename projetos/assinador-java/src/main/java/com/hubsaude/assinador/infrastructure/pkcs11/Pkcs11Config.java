package com.hubsaude.assinador.infrastructure.pkcs11;

/**
 * Representa as configurações do dispositivo PKCS#11, extraídas diretamente 
 * das seguintes variáveis de ambiente:
 * <ul>
 * <li>{@code HUBSAUDE_PKCS11_LIBRARY} — Caminho completo para a biblioteca nativa (.so ou .dll).</li>
 * <li>{@code HUBSAUDE_PKCS11_NAME}    — Nome lógico do provedor (valor padrão: "HubSaudePKCS11").</li>
 * <li>{@code HUBSAUDE_PKCS11_PIN}     — PIN ou senha para autenticação no KeyStore.</li>
 * </ul>
 *
 * <p>O método {@link #fromEnvironment()} retornará {@code null} caso a variável 
 * {@code HUBSAUDE_PKCS11_LIBRARY} não esteja definida. Esse comportamento sinaliza 
 * ao sistema que o modo simulado (fake) deve ser ativado.
 */
public record Pkcs11Config(String libraryPath, String name, char[] pin) {

    public static Pkcs11Config fromEnvironment() {
        String lib = System.getenv("HUBSAUDE_PKCS11_LIBRARY");
        if (lib == null || lib.isBlank()) {
            return null;
        }
        String name = System.getenv("HUBSAUDE_PKCS11_NAME");
        if (name == null || name.isBlank()) {
            name = "HubSaudePKCS11";
        }
        String pin = System.getenv("HUBSAUDE_PKCS11_PIN");
        return new Pkcs11Config(lib, name, pin != null ? pin.toCharArray() : new char[0]);
    }
}

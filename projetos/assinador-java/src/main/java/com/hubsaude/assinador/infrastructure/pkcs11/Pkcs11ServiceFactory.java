package com.hubsaude.assinador.infrastructure.pkcs11;

import com.hubsaude.assinador.domain.service.Pkcs11SignatureService;
import com.hubsaude.assinador.domain.service.SignatureService;

import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;

/**
 * Instancia e configura um {@link Pkcs11SignatureService} vinculado ao dispositivo 
 * PKCS#11 especificado nas definições de {@link Pkcs11Config}.
 *
 * <p>Este método inicializa o provedor SunPKCS11 utilizando a biblioteca nativa 
 * informada e realiza a abertura do KeyStore do dispositivo. Caso o provedor esteja 
 * indisponível ou o dispositivo não responda, uma exceção será disparada. O chamador 
 * deve capturar essa exceção para acionar o mecanismo de contingência 
 * (fallback) com o {@link com.hubsaude.assinador.domain.service.FakeSignatureService}.
 *
 * <p>Preparação do ambiente de testes utilizando SoftHSM2:
 * <pre>
 * softhsm2-util --init-token --slot 0 --label "hubsaude" --pin 1234 --so-pin 1234
 * export HUBSAUDE_PKCS11_LIBRARY=/usr/lib/softhsm/libsofthsm2.so
 * export HUBSAUDE_PKCS11_PIN=1234
 * </pre>
 */
public class Pkcs11ServiceFactory {

    public static SignatureService create(Pkcs11Config config) throws Exception {
        Provider provider = Security.getProvider("SunPKCS11");
        if (provider == null) {
            throw new IllegalStateException("Provedor SunPKCS11 não disponível nesta plataforma");
        }

        String pkcs11Cfg = String.format("--name=%s%nlibrary=%s%n", config.name(), config.libraryPath());
        provider = provider.configure(pkcs11Cfg);
        Security.addProvider(provider);

        KeyStore ks = KeyStore.getInstance("PKCS11", provider);
        ks.load(null, config.pin());

        return new Pkcs11SignatureService(ks);
    }
}

package com.hubsaude.assinador.domain.service;

import com.hubsaude.assinador.domain.model.SignRequest;
import com.hubsaude.assinador.domain.model.SignatureResult;
import com.hubsaude.assinador.domain.model.ValidateRequest;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;

/**
 * Implementação do {@link SignatureService} que utiliza um dispositivo criptográfico
 * por meio do provedor SunPKCS11. Cabe à classe chamadora realizar a carga prévia 
 * do KeyStore utilizando o {@link Pkcs11ServiceFactory}.
 *
 * <p>No processo de assinatura, o alias da chave privada no KeyStore PKCS#11 é 
 * localizado a partir do campo {@code token} informado no {@link SignRequest}. 
 * Já na validação, o sistema percorre os certificados do dispositivo até encontrar 
 * aquele que confirme a assinatura.
 *
 * <p>Caso ocorra alguma falha de comunicação ou acesso ao dispositivo (como ausência 
 * da biblioteca nativa ou PIN incorreto), a inicialização do KeyStore falhará no 
 * {@link Pkcs11ServiceFactory}, ativando automaticamente o mecanismo de contingência 
 * via {@link FakeSignatureService}.
 */
public class Pkcs11SignatureService implements SignatureService {

    private final KeyStore keyStore;

    public Pkcs11SignatureService(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public SignatureResult sign(SignRequest request) {
        try {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(request.getToken(), null);
            if (privateKey == null) {
                return new SignatureResult(null, false,
                        "Chave privada não encontrada no dispositivo com alias: " + request.getToken());
            }

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey);
            sig.update(request.getContent().getBytes(StandardCharsets.UTF_8));

            return new SignatureResult(
                    Base64.getEncoder().encodeToString(sig.sign()),
                    true,
                    "Assinatura criada com sucesso via PKCS#11"
            );
        } catch (Exception e) {
            return new SignatureResult(null, false, "Erro ao assinar via PKCS#11: " + e.getMessage());
        }
    }

    @Override
    public SignatureResult validate(ValidateRequest request) {
        try {
            byte[] sigBytes     = Base64.getDecoder().decode(request.getSignature());
            byte[] contentBytes = request.getContent().getBytes(StandardCharsets.UTF_8);

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                Certificate cert = keyStore.getCertificate(aliases.nextElement());
                if (cert == null) continue;
                try {
                    Signature sig = Signature.getInstance("SHA256withRSA");
                    sig.initVerify(cert.getPublicKey());
                    sig.update(contentBytes);
                    if (sig.verify(sigBytes)) {
                        return new SignatureResult(request.getSignature(), true, "Assinatura é válida");
                    }
                } catch (Exception ignored) {
                  
                    /** tenta o certificado seguinte*/
                }
            }
            return new SignatureResult(request.getSignature(), false, "Assinatura é inválida");

        } catch (IllegalArgumentException e) {
            return new SignatureResult(request.getSignature(), false,
                    "Assinatura com encoding inválido: " + e.getMessage());
        } catch (Exception e) {
            return new SignatureResult(request.getSignature(), false,
                    "Erro ao validar via PKCS#11: " + e.getMessage());
        }
    }
}

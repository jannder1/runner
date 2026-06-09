import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class RunnerSignatureUtil {

    public static String gerarAssinatura(String dados, PrivateKey chavePrivada) {
        try {
           
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(chavePrivada);
            
          
            signer.update(dados.getBytes("UTF-8"));
            
           
            return Base64.getEncoder().encodeToString(signer.sign());
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar assinatura digital: " + e.getMessage());
        }
    }
}
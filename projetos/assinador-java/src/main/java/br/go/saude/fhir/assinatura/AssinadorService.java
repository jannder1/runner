package br.go.saude.fhir.assinatura;

import br.go.saude.fhir.assinatura.model.OperationOutcome;
import br.go.saude.fhir.assinatura.model.SignatureRequest;
import br.go.saude.fhir.assinatura.model.ValidationRequest;
import br.go.saude.fhir.assinatura.model.HealthStatus;

/**
 * Serviço principal de assinatura digital simulada para FHIR
 * Conforme guia SES GO - UC-01, UC-02 e UC-03
 */
public interface AssinadorService {
    
    /**
     * Cria assinatura digital simulada para um Bundle FHIR
     * @param request Parâmetros da assinatura (bundle, provenance, config, etc)
     * @return OperationOutcome com a Signature ou erro
     */
    OperationOutcome assinar(SignatureRequest request);
    
    /**
     * Valida uma assinatura digital simulada
     * @param request Parâmetros da validação (signature, config, bundle opcional)
     * @return OperationOutcome com resultado da validação
     */
    OperationOutcome validar(ValidationRequest request);
    
    /**
     * Verifica saúde do serviço
     * @return Status do serviço
     */
    HealthStatus healthCheck();
}
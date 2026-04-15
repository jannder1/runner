package br.go.saude.fhir.assinatura.model;

public class ValidationRequest {
    private String signatureData;
    private String bundleFhirJson;
    private String provenanceJson;
    private boolean verificarIntegridade;
    
    public ValidationRequest() {}
    
    public String getSignatureData() { return signatureData; }
    public void setSignatureData(String signatureData) { this.signatureData = signatureData; }
    
    public String getBundleFhirJson() { return bundleFhirJson; }
    public void setBundleFhirJson(String bundleFhirJson) { this.bundleFhirJson = bundleFhirJson; }
    
    public String getProvenanceJson() { return provenanceJson; }
    public void setProvenanceJson(String provenanceJson) { this.provenanceJson = provenanceJson; }
    
    public boolean isVerificarIntegridade() { return verificarIntegridade; }
    public void setVerificarIntegridade(boolean verificarIntegridade) { this.verificarIntegridade = verificarIntegridade; }
}
package br.go.saude.fhir.assinatura.model;

public class SignatureRequest {
    private String bundleFhirJson;
    private String provenanceJson;
    private String policyUri;
    private Long timestamp;
    private String estrategia;
    private String certificadoJson;
    
    public SignatureRequest() {}
    
    public String getBundleFhirJson() { return bundleFhirJson; }
    public void setBundleFhirJson(String bundleFhirJson) { this.bundleFhirJson = bundleFhirJson; }
    
    public String getProvenanceJson() { return provenanceJson; }
    public void setProvenanceJson(String provenanceJson) { this.provenanceJson = provenanceJson; }
    
    public String getPolicyUri() { return policyUri; }
    public void setPolicyUri(String policyUri) { this.policyUri = policyUri; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    
    public String getEstrategia() { return estrategia; }
    public void setEstrategia(String estrategia) { this.estrategia = estrategia; }
    
    public String getCertificadoJson() { return certificadoJson; }
    public void setCertificadoJson(String certificadoJson) { this.certificadoJson = certificadoJson; }
}
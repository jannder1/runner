package br.go.saude.fhir.assinatura.model;

public class ConfigOperacional {
    private int maxEntriesBundle = 1000;
    private int timestampToleranceSeconds = 300;
    private String tsaUrl = "";
    private String revocationPolicy = "strict";
    
    public ConfigOperacional() {}
    
    public int getMaxEntriesBundle() { return maxEntriesBundle; }
    public void setMaxEntriesBundle(int maxEntriesBundle) { this.maxEntriesBundle = maxEntriesBundle; }
    
    public int getTimestampToleranceSeconds() { return timestampToleranceSeconds; }
    public void setTimestampToleranceSeconds(int timestampToleranceSeconds) { this.timestampToleranceSeconds = timestampToleranceSeconds; }
    
    public String getTsaUrl() { return tsaUrl; }
    public void setTsaUrl(String tsaUrl) { this.tsaUrl = tsaUrl; }
    
    public String getRevocationPolicy() { return revocationPolicy; }
    public void setRevocationPolicy(String revocationPolicy) { this.revocationPolicy = revocationPolicy; }
}
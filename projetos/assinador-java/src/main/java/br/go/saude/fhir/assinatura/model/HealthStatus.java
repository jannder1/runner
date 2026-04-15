package br.go.saude.fhir.assinatura.model;

public class HealthStatus {
    private String status;
    private String version = "1.0.0";
    private long uptime;
    
    public HealthStatus() {}
    
    public static HealthStatus up() {
        HealthStatus status = new HealthStatus();
        status.setStatus("UP");
        status.setUptime(System.currentTimeMillis());
        return status;
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public long getUptime() { return uptime; }
    public void setUptime(long uptime) { this.uptime = uptime; }
}
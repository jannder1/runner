package br.go.saude.fhir.assinatura.model;

import java.util.ArrayList;
import java.util.List;

public class OperationOutcome {
    private String resourceType = "OperationOutcome";
    private List<Issue> issue = new ArrayList<>();
    
    public static class Issue {
        private String severity;
        private String code;
        private String diagnostics;
        
        public Issue() {}
        
        public Issue(String severity, String code, String diagnostics) {
            this.severity = severity;
            this.code = code;
            this.diagnostics = diagnostics;
        }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getDiagnostics() { return diagnostics; }
        public void setDiagnostics(String diagnostics) { this.diagnostics = diagnostics; }
    }
    
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    
    public List<Issue> getIssue() { return issue; }
    public void setIssue(List<Issue> issue) { this.issue = issue; }
    
    public static OperationOutcome success() {
        OperationOutcome outcome = new OperationOutcome();
        Issue issue = new Issue("information", "VALIDATION.SUCCESS", "Assinatura válida");
        outcome.issue.add(issue);
        return outcome;
    }
    
    public static OperationOutcome policyMissing() {
        OperationOutcome outcome = new OperationOutcome();
        Issue issue = new Issue("error", "POLICY.MISSING", "URI da política não fornecida");
        outcome.issue.add(issue);
        return outcome;
    }
    
    public static OperationOutcome policyUriInvalid(String uri) {
        OperationOutcome outcome = new OperationOutcome();
        Issue issue = new Issue("error", "POLICY.URI-INVALID", "URI inválida: " + uri);
        outcome.issue.add(issue);
        return outcome;
    }
}
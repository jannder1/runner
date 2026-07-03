# Features BDD

Esta pasta contém os arquivos .feature (Gherkin) que especificam o
comportamento esperado da CLI e do JAR em linguagem executável.

## Status atual

| Feature | Componente | Comando testado | Status |
|---|---|---|---|
| cli_sign_create.feature | CLI Runner | ssinatura sign --content X [--local] | ? reescrito |
| cli_sign_validate.feature | CLI Runner | ssinatura validate --content X --signature Y [--local] | ? reescrito |
| error_scenarios.feature | CLI + JAR | health check, erros transversais | ? válido |
| sign_create.feature | Assinador (HTTP) | POST /sign | ?? revisar |
| sign_validate.feature | Assinador (HTTP) | POST /validate | ?? revisar |

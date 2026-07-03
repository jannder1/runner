[features-README (1).md](https://github.com/user-attachments/files/29625752/features-README.1.md)
# Features BDD

Esta pasta contém os arquivos `.feature` (Gherkin) que especificam o
comportamento esperado da CLI e do JAR em linguagem executável.

## Status atual

| Feature | Componente | Comando testado | Status |
|---|---|---|---|
| `cli_sign_create.feature` | CLI Runner | `assinatura sign --content X [--local]` | ✅ reescrito |
| `cli_sign_validate.feature` | CLI Runner | `assinatura validate --content X --signature Y [--local]` | ✅ reescrito |
| `error_scenarios.feature` | CLI + JAR | health check, erros transversais | ✅ válido |
| `sign_create.feature` | Assinador (HTTP) | `POST /sign` | ⚠️ revisar |
| `sign_validate.feature` | Assinador (HTTP) | `POST /validate` | ⚠️ revisar |

## Como rodar (futuro)

```bash
# CLI Runner (subprocess)
go test ./cmd -run TestSign_*

# Assinador (HTTP + Cucumber)
cd projetos/assinador-java
mvn test -Dtest=CucumberTest
```

> **Nota:** o Cucumber não está configurado no `pom.xml` nem no
> `ci.yml` hoje. Os arquivos `.feature` são validados apenas por
> **sintaxe Gherkin** (presença de `Funcionalidade:` e `Cenário:`) no
> job `validate-bdd` do CI. Pra rodar de verdade, adicionar
> `cucumber-jvm` no `pom.xml` e configurar o runner.

## Convenção de escrita

Cada `.feature` deve:

- Começar com `# language: pt` (Gherkin em português)
- Citar o **comando real** da CLI (não criar subcomandos fictícios)
- Usar `Funcionalidade:` para o título
- Usar `Cenário:` para casos
- Marcar cenários críticos com `@critical`
- Quando o cenário depende de estado externo, usar `Contexto:` com `Dado que...`

## Última atualização

Julho/2026 — reescritos `cli_sign_create.feature` e `cli_sign_validate.feature`
para refletirem os comandos reais `assinatura sign` e `assinatura validate`
(em vez dos inexistentes `sign create` e `sign validate` da v1.0).

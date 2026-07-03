# Diagramas C4 — Sistema Runner

> **Documento:** Diagramas arquiteturais (modelo C4)
> **Projeto:** Sistema Runner — CLI + JAR de assinatura simulada
> **Última atualização:** 2026-07-02

Este documento contém os 4 níveis do modelo C4 aplicados ao Sistema Runner.
A fonte é Mermaid (renderiza direto no GitHub, GitLab, VS Code).

**Convenções:**

- Caixas com `[]` representam sistemas externos.
- Caixas com `()` representam containers/processos do nosso sistema.
- Setas sólidas (`-->`) = chamadas síncronas. Setas tracejadas (`-->` com `--` ASCII) = assíncronas ou arquivos.
- Decisões citadas (ex.: ADR-001) referenciam `docs/adr/`.

---

## C3 — Componentes da CLI Runner

Zoom dentro do binário Go.

```mermaid
C4Component
    title C3 — CLI Runner (Componentes)

    Container_Boundary(cli, "CLI Runner") {
        Component(root, "rootCmd", "Cobra.Command", "Comando raiz; registra subcomandos; processa flags globais")
        Component(version, "versionCmd", "Cobra.Command", "Imprime versão (tag + SHA curto)")
        Component(sign, "signCmd", "Cobra.Command", "Cria assinatura (default: HTTP; --local: subprocess)")
        Component(validate, "validateCmd", "Cobra.Command", "Valida assinatura existente")
        Component(start, "startCmd", "Cobra.Command", "Sobe JAR em background (idempotente)")
        Component(stop, "stopCmd", "Cobra.Command", "Encerra JAR (sinal ou endpoint)")
        Component(run, "runCmd", "Cobra.Command", "Atalho: start + comando + stop")

        Component(httpClient, "httpClient", "net/http", "Cliente HTTP para o JAR em modo servidor")
        Component(procRunner, "procRunner", "os/exec", "Wrapper para `java -jar assinador.jar` em modo local")
        Component(discovery, "discovery", "Cliente TCP + GET /health", "Implementa ADR-003")
        Component(provision, "jdkProvisioner", "Implementação interna", "Provisiona JDK se ausente (primeira execução)")
    }

    Rel(usuario, root, "Invoca", "argv")
    Rel(root, version, "Dispatch")
    Rel(root, sign, "Dispatch")
    Rel(root, validate, "Dispatch")
    Rel(root, start, "Dispatch")
    Rel(root, stop, "Dispatch")
    Rel(root, run, "Dispatch")

    Rel(sign, httpClient, "Modo servidor")
    Rel(sign, procRunner, "Modo --local")
    Rel(validate, httpClient, "Modo servidor")
    Rel(validate, procRunner, "Modo --local")
    Rel(start, discovery, "Verifica instância viva antes de subir")
    Rel(start, procRunner, "Sobe JAR se necessário")
    Rel(stop, httpClient, "Shutdown via endpoint ou sinal")
    Rel(run, start, "Encadeia")
    Rel(run, sign, "Encadeia")
    Rel(run, stop, "Encadeia")

    Rel(start, provision, "Garante JDK disponível")
```

**Componentes:**

| Componente | Responsabilidade |
|---|---|
| `rootCmd` + subcomandos | Parsing de argv; help automático; flags persistentes |
| `httpClient` | HTTP ao JAR em modo servidor |
| `procRunner` | Subprocesso `java -jar` em modo local |
| `discovery` | Sondagem TCP + health check (ADR-003) |
| `jdkProvisioner` | Provisionamento sob demanda |

**Decisões refletidas neste nível:**

- Parser de CLI: Cobra — ADR-006.
- Modo servidor como default — ADR-002.
- Descoberta de instância viva — ADR-003.

---

## Notas para revisão

- **Diagrama de Classes (C4)** foi omitido de propósito: o modelo C4 desencoraja
  esse nível porque diagramas UML de classes detalhados tendem a ficar
  desatualizados rápido e trazem pouco valor. Os próprios arquivos-fonte são
  a fonte da verdade nesse nível.
- Os diagramas acima são **renderizados em Mermaid**. Para editar:
  qualquer editor Mermaid online (mermaid.live) ou a extensão
  "Markdown Preview Mermaid Support" no VS Code.
- Toda decisão arquitetural citada (ADR-001 a 006) está registrada em
  `docs/adr/` e tem commit próprio.
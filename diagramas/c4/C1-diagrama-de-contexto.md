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

## C1 — Diagrama de Contexto

Mostra o Sistema Runner como uma caixa-preta no centro, os atores que o
usam e os sistemas externos com os quais ele troca dados.

```mermaid
C4Context
    title C1 — Sistema Runner (Contexto)

    Person(usuario, "Usuário", "Pessoa que invoca a CLI para assinar/validar documentos simulados")

    System(runner, "Sistema Runner", "CLI + JAR que abstrai execução Java e simula assinatura digital")

    System_Ext(pkcs11, "Token / Smart Card PKCS#11", "Dispositivo criptográfico real (opcional, modo PKCS#11)")
    System_Ext(jdk, "JDK / JVM", "Ambiente Java provisionado ou pré-instalado")
    System_Ext(hubsaude, "Simulador HubSaúde", "Aplicação externa gerenciada pela CLI (start/stop/status)")

    Rel(usuario, runner, "Invoca comandos", "Terminal/Shell")
    Rel(runner, jdk, "Executa JAR via", "Subprocess")
    Rel(runner, hubsaude, "Gerencia ciclo de vida", "HTTP / sinal")
    Rel(runner, pkcs11, "Acessa (modo PKCS#11)", "Biblioteca nativa")
```

**Atores / sistemas:**

| Elemento | Tipo | Relação com o Runner |
|---|---|---|
| Usuário | Humano | Invoca `assinatura ...` no shell |
| JDK/JVM | Sistema externo (dependência) | Hospeda o `assinador.jar` |
| Token PKCS#11 | Sistema externo (opcional) | Usado quando modo PKCS#11 ativo |
| Simulador HubSaúde | Sistema externo (parceria) | Ciclo de vida controlado pela CLI |

---


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

## C2 — Diagrama de Contêineres

Zoom no Runner: separa os processos executáveis e como se conversam.

```mermaid
C4Container
    title C2 — Sistema Runner (Contêineres)

    Person(usuario, "Usuário", "Shell")

    System_Boundary(runner, "Sistema Runner") {
        Container(cli, "CLI Runner", "Go 1.26 + Cobra", "Binário multiplataforma que orquestra o JAR; oferece comandos sign/validate/start/stop/simulator/version")
        Container(jar, "assinador.jar", "Java 21 + Spring Boot 3.3.5", "Aplicação web embedded (Tomcat) que valida parâmetros FHIR e simula operações de assinatura")
        ContainerDb(pidfile, "PID/porta file", "Arquivo texto", "Estado efêmero para descoberta de instância viva")
    }

    System_Ext(jvm, "JVM", "Java 21 runtime")
    System_Ext(pkcs11, "Token PKCS#11", "Hardware criptográfico")
    System_Ext(hubsaude, "Simulador HubSaúde", "Aplicação externa")

    Rel(usuario, cli, "Invoca", "argv/stdin/stdout/stderr")
    Rel(cli, jvm, "Sobe JAR via", "java -jar assinador.jar")
    Rel(jvm, jar, "Carrega e executa", "in-process")
    Rel(cli, jar, "HTTP (modo servidor, porta 8080)", "JSON over HTTP")
    Rel(cli, jar, "stdout/stderr (modo local)", "Subprocess pipe")
    Rel(cli, jar, "Lê/escreve", pidfile)
    Rel(cli, hubsaude, "start/stop/status/health", "HTTP")
    Rel(jar, pkcs11, "Assina via (modo PKCS#11)", "Biblioteca nativa C")

    UpdateRelStyle(cli, jar, $textColor="blue", $lineStyle="solid")
    UpdateLayoutConfig($c4ShapeInRow="3")
```

**Containers:**

| Container | Tecnologia | Responsabilidade |
|---|---|---|
| **CLI Runner** | Go 1.26 + Cobra | Interface com usuário; orquestração; ciclo de vida; provisionamento JDK |
| **assinador.jar** | Java 21 + Spring Boot 3.3.5 | Validação de entrada (autoridade única — ver ADR-004); simulação de assinatura; exposição HTTP |
| **PID/porta file** | Arquivo texto | Estado para descoberta de instância viva (ver ADR-003) |

**Decisões refletidas neste nível:**

- Porta padrão 8080, configurável via `--port` (ADR-001).
- Modo servidor como default (ADR-002).
- Descoberta de instância viva via TCP + health check (ADR-003).

---


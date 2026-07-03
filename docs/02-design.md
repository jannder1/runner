[02-design-v2.md](https://github.com/user-attachments/files/29624602/02-design-v2.md)
# Sistema Runner — Design

> **Documento:** Design da Arquitetura
> **Disciplina:** Implementação e Integração (2026-01)
> **Versão:** 2.0 (consertada para refletir o código real)
> **Relacionado:** [01-especificacao.md](./01-especificacao.md) · [ADRs em `docs/adr/`](./adr/README.md) · [Diagramas C4 em `diagramas/c4/`](../diagramas/c4/README.md)

> ⚠️ **Atenção — esta versão foi reescrita.** A versão 1.0 continha arte
> ASCII corrompida nos diagramas, citava stack errado (Java com
> `com.sun.net.httpserver` em vez de Spring Boot), listava comandos
> inexistentes (`sign create`, `simulator start`), e documentava um
> payload de `/health` que o código não retorna. Esta versão 2.0
> referencia o código real e linka para os diagramas C4 e ADRs já
> existentes em outras pastas.

---

## 1. Visão Geral da Arquitetura

O Sistema Runner é composto por **três binários** independentes:

| Binário | Tecnologia | Stack | Distribuição | Responsabilidade |
|---|---|---|---|---|
| **`assinatura`** | CLI Runner | Go 1.26 + Cobra | Binário único pré-compilado | Interface com usuário, orquestração |
| **`assinador.jar`** | Assinador | Java 21 + Spring Boot 3.3.5 (Tomcat embedded) | uberjar | Validação de entrada, simulação de assinaturas |
| **`simulador`** | Simulador HubSaúde | Go 1.26 + Cobra (CLI próprio) | Binário único pré-compilado | CLI para gerenciar `simulador.jar` externo |

> **Observação:** o **Simulador HubSaúde** é gerenciado por um CLI próprio
> (`simulador start/stop/status`), **não** como subcomando de `assinatura`.
> A versão 1.0 deste design listava `simulator start` como subcomando da
> CLI Runner — isso nunca foi implementado. O CLI correto é o binário
> `simulador`.

### 1.1. Princípios Arquiteturais

1. **Separação de responsabilidades:** CLI não implementa regras de negócio de assinatura (ver ADR-004).
2. **Stateless onde possível:** Lógica de negócio é stateless; estado é só o pid file em `~/.hubsaude/`.
3. **Falha explícita:** Erros com mensagens claras (o quê? por quê? como resolver?), códigos de saída distintos para erro do usuário vs erro do sistema.
4. **Configurabilidade mínima:** Defaults sensatos, override via flags e env vars.
5. **Distribuição simples:** Binário único por plataforma, JAR único.

---

## 2. Diagramas C4

> Os diagramas vivem em [`diagramas/c4/`](../diagramas/c4/README.md) e são
> renderizados em Mermaid. Esta seção referencia-os e descreve em
> palavras o que cada um mostra.

### 2.1. C1 — Contexto

Arquivo: [`diagramas/c4/C1-diagrama-de-contexto.md`](../diagramas/c4/C1-diagrama-de-contexto.md)

Mostra o Sistema Runner como caixa-preta no centro, com:
- **Usuário** (ator) — invoca `assinatura` no shell.
- **JDK/JVM** — hospeda o `assinador.jar`.
- **Token PKCS#11** — dispositivo opcional acessado em modo PKCS#11.
- **Simulador HubSaúde** — sistema externo gerenciado pela CLI `simulador`.

### 2.2. C2 — Contêineres

Arquivo: [`diagramas/c4/C2-diagrama-de-conteineres.md`](../diagramas/c4/C2-diagrama-de-conteineres.md)

Mostra os **containers executáveis**:
- CLI `assinatura` (Go)
- `assinador.jar` (Java + Spring Boot)
- CLI `simulador` (Go)
- Arquivo `pid` em `~/.hubsaude/assinador.pid`

E como se comunicam: subprocesso (`java -jar`), HTTP (`/sign`, `/validate`, `/health`), leitura/escrita do pid file.

### 2.3. C3 — Componentes do `assinador.jar`

Arquivo: [`diagramas/c4/C3-componentes-jar.md`](../diagramas/c4/C3-componentes-jar.md)

Zoom dentro do JAR: camadas `bootstrap` (AssinadorApplication, WebApplication, ServerStartupHandler), `presentation` (controllers HTTP, DTOs, exception handler), `application` (use cases + validator), `domain` (modelos e serviços), `infrastructure` (PKCS#11, JSON, inatividade).

### 2.4. C3 — Componentes da CLI Runner

Arquivo: [`diagramas/c4/C3-componentes-da-cli-runner.md`](../diagramas/c4/C3-componentes-da-cli-runner.md)

Zoom dentro do binário Go: `rootCmd` + subcomandos (`version`, `sign`, `validate`, `start`, `stop`, `run`), clientes internos (`httpClient`, `procRunner`, `discovery`, `jdkProvisioner`).

---

## 3. Decisões Arquiteturais (ADRs)

Os ADRs vivem em [`docs/adr/`](./adr/README.md) como arquivos individuais,
versionados junto do código. Este documento apenas referencia-os:

| # | Decisão | Status |
|---|---|---|
| [ADR-001](./adr/ADR-001-porta-padrao-http.md) | Porta padrão do servidor HTTP (8080, configurável) | Aceito |
| [ADR-002](./adr/ADR-002-modo-servidor-como-padrao.md) | Modo servidor como default da CLI | Aceito |
| [ADR-003](./adr/ADR-003-descoberta-instancia-viva.md) | Descoberta de instância viva (TCP + health check) | Aceito |
| [ADR-004](./adr/ADR-004-validacao-parametros-autoridade-unica.md) | Validação: autoridade única no JAR | Aceito |
| [ADR-005](./adr/ADR-005-auto-shutdown-inatividade.md) | Auto-shutdown por inatividade | Aceito |
| [ADR-006](./adr/ADR-006-parser-cli-cobra.md) | Parser de CLI em Cobra | Aceito |

> A versão 1.0 deste design listava 6 ADRs próprios (linguagem Go, linguagem
> Java, etc.). Esses foram **removidos** daqui porque já estão implícitos no
> `pom.xml`, no `go.mod` e nas ADRs acima. **Decisões registradas devem ser
> as não-óbvias**, não as triviais.

---

## 4. Contratos de Interface

### 4.1. CLI `assinatura`

```
assinatura <subcomando> [flags]

Subcomandos:
  version             Exibe versão do CLI
  sign                Cria uma assinatura simulada
  validate            Valida uma assinatura simulada
  start               Sobe o assinador.jar em background (modo servidor)
  stop                Encerra o assinador.jar em background
  run                 Atalho: start + comando + stop em uma invocação
```

**Flags relevantes:**

| Subcomando | Flag | Default | Descrição |
|---|---|---|---|
| `sign` | `--content` | (obrigatório) | Conteúdo a ser assinado |
| `sign` | `--local` | false | Força modo subprocesso |
| `validate` | `--content` | (obrigatório) | Conteúdo original |
| `validate` | `--signature` | (obrigatório) | Assinatura a validar |
| `validate` | `--local` | false | Força modo subprocesso |
| `start` | `--port` | 8080 | Porta HTTP |
| `start` | `--timeout` | 0 | Inatividade em minutos (0 = desativa) |
| `stop` | `--port` | 0 (usa pid file) | Porta esperada |

**Exit codes:**

| Código | Significado |
|---|---|
| 0 | Sucesso |
| 1 | Erro genérico |
| 2 | Uso incorreto (Cobra) |
| 64 | Argumentos inválidos (`EX_USAGE`) |
| 65 | Dados inválidos (`EX_DATAERR`) |
| 70 | Erro interno (`EX_SOFTWARE`) |
| 75 | Falha temporária (`EX_TEMPFAIL`) — health check falhou |
| 78 | Erro de configuração (`EX_CONFIG`) |
| 130 | Ctrl+C |

### 4.2. CLI `simulador`

```
simulador <subcomando> [flags]

Subcomandos:
  version             Exibe versão do CLI
  start               Sobe o simulador.jar em background
  stop                Encerra o simulador.jar em background
  status              Exibe status do simulador
```

> Gerencia `simulador.jar` (sistema externo HubSaúde). Detalhes em
> `projetos/simulador/`.

### 4.3. Assinador — API HTTP

**Base URL:** `http://localhost:<porta>` (default 8080)

#### `GET /health`

Resposta (real, conforme `HealthController.java`):
```json
{ "status": "UP" }
```

> A versão 1.0 deste design documentava um payload com `uptime_seconds` e
> `requests_handled`. Esses campos **não existem** no código atual. Se forem
> adicionados no futuro, atualizar aqui.

#### `POST /sign`

Request:
```json
{
  "content": "texto a assinar",
  "token": "opcional"
}
```

Response (`SignatureHttpResponse`):
```json
{
  "signature": "RUNNER_SIM_SIG_...",
  "valid": true,
  "message": "Assinatura simulada criada com sucesso"
}
```

#### `POST /validate`

Request:
```json
{
  "content": "texto original",
  "signature": "RUNNER_SIM_SIG_..."
}
```

Response:
```json
{
  "signature": "RUNNER_SIM_SIG_...",
  "valid": true,
  "message": "Assinatura válida"
}
```

Erros: HTTP 400 com `GlobalExceptionHandler` retornando corpo JSON estruturado.

### 4.4. Modo CLI standalone do JAR

```
java -jar assinador.jar sign --content "texto"
java -jar assinador.jar validate --content "texto" --signature "SIG"
java -jar assinador.jar serve --port 8080
```

A bifurcação é decidida pelo primeiro argumento em `AssinadorApplication.main`:
- `serve` → modo servidor HTTP (Spring Boot).
- `sign` / `validate` → modo CLI standalone (`CliRunner`).
- outro / nenhum → erro.

---

## 5. Camadas do `assinador.jar`

```
┌─────────────────────────────────────────┐
│ Bootstrap                                │
│   AssinadorApplication, WebApplication,  │
│   ServerStartupHandler                   │
├─────────────────────────────────────────┤
│ Presentation                             │
│   HTTP: HealthController,                │
│         SignatureController,             │
│         GlobalExceptionHandler, DTOs     │
│   CLI:   CliRunner, CliPresenter         │
├─────────────────────────────────────────┤
│ Application (use cases)                  │
│   SignUseCase, ValidateUseCase,          │
│   RequestValidator, ValidationException  │
├─────────────────────────────────────────┤
│ Domain                                   │
│   SignRequest, ValidateRequest,          │
│   SignatureResult,                       │
│   SignatureService (interface),          │
│   FakeSignatureService,                  │
│   Pkcs11SignatureService                 │
├─────────────────────────────────────────┤
│ Infrastructure                           │
│   AppConfig, Pkcs11Config,               │
│   Pkcs11ServiceFactory, JsonMapper,      │
│   RequestTimestamp, InactivityFilter,    │
│   InactivityShutdown                     │
└─────────────────────────────────────────┘
```

Princípio: dependências apontam **para dentro** (Infrastructure → Application → Domain; Presentation → Application). Domain não conhece nada externo.

---

## 6. Onde ficam os arquivos de estado

| Arquivo | Caminho | Quem escreve | Quem lê |
|---|---|---|---|
| `assinador.pid` | `~/.hubsaude/assinador.pid` | `assinatura start` | `assinatura sign/validate/stop` |
| `assinador.jar` | `~/.hubsaude/assinador.jar` | provisionador | CLI e JAR |
| `JDK` provisionado | `~/.hubsaude/jdk/` | provisionador | `java` invocado pela CLI |
| `simulador.pid` | `~/.hubsaude/simulador.pid` | `simulador start` | `simulador stop/status` |

---

## 7. Variáveis de ambiente

| Variável | Consumida por | Efeito | Default |
|---|---|---|---|
| `HUBSAUDE_TIMEOUT_MINUTES` | `assinador.jar` (modo servidor) | Janela de auto-shutdown em minutos. Ausente ou `0` = desativado. | desativado |

---

## 8. Histórico de versões

| Versão | Data | Mudanças |
|---|---|---|
| 1.0 | jun/2026 | Versão inicial (com erros) |
| 2.0 | jul/2026 | Diagramas ASCII corrompidos substituídos por referências Mermaid. ADR-001 a 006 movidos para `docs/adr/`. Comandos inexistentes removidos (`sign create`, `sign validate`, `simulator start`). Payload `/health` corrigido. ADR-002 atualizado para refletir Spring Boot. Documentado o CLI `simulador` separado. |

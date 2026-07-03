[projetos-README.md](https://github.com/user-attachments/files/29625650/projetos-README.md)
# projetos/

Este diretório contém os **subprojetos** do Sistema Runner. Cada subprojeto
é um módulo independente, com sua própria toolchain, testes e release.

## Layout

```
projetos/
├── README.md                  # Este arquivo
├── go.work                    # Go workspace file (declara os módulos Go)
├── assinatura/                # CLI Runner (Go + Cobra)
├── assinador-java/            # Assinador (Java 21 + Spring Boot)
├── simulador/                 # CLI do Simulador HubSaúde (Go + Cobra)
└── shared/                    # Código compartilhado entre os CLIs Go
```

## Subprojetos

### [`assinatura/`](./assinatura/) — CLI Runner

Binário Go que o usuário invoca no terminal. Comandos: `version`, `sign`,
`validate`, `start`, `stop`, `run`.

- **Linguagem:** Go 1.26+
- **Framework CLI:** Cobra
- **Módulo:** `github.com/jannder1/runner/assinatura`
- **Build:** `go build -o bin/assinatura ./cmd/assinatura`
- **Release:** GoReleaser → binários `assinatura-{os}-{arch}` + `SHA256SUMS`

### [`assinador-java/`](./assinador-java/) — Assinador

Aplicação Java que valida parâmetros e simula operações de assinatura.
Pode rodar em modo CLI standalone (`java -jar assinador.jar sign ...`)
ou em modo servidor HTTP (`java -jar assinador.jar serve --port 8080`).

- **Linguagem:** Java 21+
- **Framework:** Spring Boot 3.3.5 (Tomcat embedded)
- **Build:** `mvn clean package` → `target/assinador.jar`
- **Endpoints HTTP:** `/health`, `/sign`, `/validate`
- **Releases:** uberjar distribuído via GitHub Releases

### [`simulador/`](./simulador/) — CLI do Simulador HubSaúde

Binário Go separado que gerencia o ciclo de vida do `simulador.jar`
(sistema externo que representa o HubSaúde). **Não** é subcomando de
`assinatura`.

- **Linguagem:** Go 1.26+
- **Framework CLI:** Cobra
- **Módulo:** `github.com/jannder1/runner/simulador`
- **Comandos:** `version`, `start`, `stop`, `status`
- **Build:** `go build -o bin/simulador ./cmd/simulador`

### [`shared/`](./shared/) — código compartilhado Go

Pacotes reutilizados por `assinatura/` e `simulador/`:

| Pacote | Responsabilidade |
|---|---|
| `config` | Caminhos compartilhados sob `~/.hubsaude/` |
| `jre` | Provisionamento de JDK |
| `process` | Detach de processos (fork/exec multiplataforma) |
| `release` | Fetch e parse de `release.json` |

- **Módulo:** `github.com/jannder1/runner/shared`

## Go workspace

`projetos/go.work` declara os três módulos Go (`assinatura`, `simulador`,
`shared`) para que builds locais funcionem sem precisar publicar cada um
separadamente. Para trabalhar num subprojeto:

```bash
cd projetos/assinatura      # ou simulador, ou shared
go test ./...
go build ./...
```

Para buildar todos de uma vez a partir da raiz:

```bash
cd projetos
go build ./...
```

## Convenções entre subprojetos

- **Estado:** arquivos de runtime (`*.pid`, JARs cacheados, JDK provisionado)
  ficam em `~/.hubsaude/` (Linux/macOS) ou `%USERPROFILE%\.hubsaude\` (Windows).
- **Exit codes:** padronizados conforme `docs/02-design.md` §4.1.
- **Versionamento:** SemVer. Tags como `v0.1.0` disparam release automatizado.
- **Commits:** Conventional Commits. Mensagens linkam issue quando aplicável.

## Status

| Subprojeto | Estado | Última versão |
|---|---|---|
| `assinatura` | Funcional | dev |
| `assinador-java` | Funcional | 0.1.0 |
| `simulador` | Funcional | dev |
| `shared` | Estável | v0.0.0 |

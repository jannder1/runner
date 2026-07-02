[README Principal.md](https://github.com/user-attachments/files/29272421/README.Principal.md)
# Sistema Runner

> CLI multiplataforma para simulação de assinatura digital — Trabalho prático de **Implementação e Integração (2026-01)**, Bacharelado em Engenharia de Software — UFG.
>
> **Professor:** Dr. Fabio Lucena

---

## O que é isso?

O **Sistema Runner** é uma CLI multiplataforma que abstrai a complexidade de executar aplicações Java relacionadas a assinatura digital simulada. O sistema é composto por:

- **CLI Runner** (`assinatura`): binário Go multiplataforma que o usuário final invoca
- **Assinador** (`assinador.jar`): aplicação Java que valida parâmetros FHIR e simula operações de assinatura

> ⚠️ Este sistema **simula** operações de assinatura digital. Não é adequado para uso em produção com dados reais.

---

## Instalação Rápida

Baixe o binário pré-compilado para sua plataforma na página de [Releases](../../releases):

| Plataforma | Binário |
|------------|---------|
| Windows (amd64) | `assinatura-windows-amd64.exe` |
| Linux (amd64) | `assinatura-linux-amd64` |
| macOS (amd64) | `assinatura-darwin-amd64` |

### Verificação de integridade

Após o download, verifique o checksum SHA256:

```bash
# Linux / macOS
sha256sum -c SHA256SUMS

# Windows (PowerShell)
Get-FileHash assinatura-windows-amd64.exe -Algorithm SHA256
```

---

## Uso Básico

O Sistema Runner tem dois modos de operação, controlados automaticamente:

- **Modo servidor (padrão):** a CLI sobe (ou reusa) o `assinador.jar` em background
  e fala com ele via HTTP. Várias chamadas seguidas têm baixa latência.
- **Modo local (`--local`):** a CLI invoca `java -jar assinador.jar` uma vez por
  comando. Mais lento (cold start da JVM), mas não deixa processo em background.

```bash
# Versão do CLI
assinatura version

# Criar assinatura simulada (modo servidor se houver instância ativa)
assinatura sign --content "meu documento"

# Forçar modo local (subprocess, sem servidor)
assinatura sign --content "meu documento" --local

# Validar assinatura simulada
assinatura validate --content "meu documento" --signature "RUNNER_SIM_SIG_..."

# Subir o assinador.jar em background (idempotente — reusa instância viva)
assinatura start
assinatura start --port 9090
assinatura start --port 9090 --timeout 30   # auto-shutdown após 30min sem requisições

# Encerrar o servidor em background
assinatura stop
assinatura stop --port 9090

# Atalho: start + comando + stop (uma invocação só)
assinatura run sign --content "documento único"

# Ajuda
assinatura --help
assinatura sign --help
```

> 💡 Na primeira execução, o sistema provisiona automaticamente o JDK necessário.

### Onde ficam os arquivos de estado

| Arquivo | Caminho | Propósito |
|---|---|---|
| `assinador.pid` | `~/.hubsaude/assinador.pid` | Registra PID e porta do servidor ativo. Lido por `sign`, `validate` e `stop` para descobrirem a instância. |
| `assinador.jar` | `~/.hubsaude/assinador.jar` | Cópia local do JAR quando baixado automaticamente. |
| `JDK` provisionado | `~/.hubsaude/jdk/` | JDK baixado na primeira execução (se necessário). |

### Variáveis de ambiente

| Variável | Consumida por | Efeito |
|---|---|---|
| `HUBSAUDE_TIMEOUT_MINUTES` | `assinador.jar` | Janela de inatividade para auto-shutdown (em minutos). Ausente ou `0` = desativado. Injetada pela CLI quando você passa `assinatura start --timeout N`. |

### Códigos de saída

| Código | Significado |
|---|---|
| `0` | Sucesso |
| `1` | Erro genérico (CLI ou JAR) |
| `65` (`EX_DATAERR`) | Erro de validação do usuário (entrada malformada) |
| `70` (`EX_SOFTWARE`) | Erro interno do software |
| `75` (`EX_TEMPFAIL`) | Falha temporária (ex.: health check falhou, JAR não subiu) |

### Quando você recebe "nenhum servidor registrado"

Esse erro vem de `stop`/`sign`/`validate` quando o pid file
(`~/.hubsaude/assinador.pid`) não existe ou está corrompido. Causas comuns:

1. Você nunca rodou `assinatura start`.
2. O servidor foi encerrado por `kill -9` (não houve shutdown gracioso).
3. Outra pessoa rodou `start` em outro usuário do sistema.

Solução: rode `assinatura start` para criar o registro novamente.


## Estrutura do Repositório

```
runner/
├── docs/                          # Documentação do projeto
│   ├── 01-especificacao.md        # Especificação de requisitos
│   ├── 02-design.md               # Arquitetura e decisões (C4)
│   ├── 03-plano-implementacao.md  # Plano de sprints
│   └── 04-backlog/                # Export de issues (opcional)
├── projetos/
│   ├── assinatura/                # CLI Runner (Go + Cobra)
│   └── assinador-java/            # Assinador (Java 21)
├── diagramas/                     # Diagramas C4 (SVG + fonte Mermaid)
├── .github/
│   ├── workflows/                 # CI/CD
│   └── ISSUE_TEMPLATE/            # Templates de issues
└── README.md                      # Este arquivo
```

---

## Documentação Completa

- 📋 [Especificação](docs/01-especificacao.md) — requisitos, escopo, casos de uso
- 🏗️ [Design](docs/02-design.md) — arquitetura, C4, ADRs, contratos
- 📅 [Plano de Implementação](docs/03-plano-implementacao.md) — sprints, tasks, DoR/DoD
- 🎯 [Diagramas C4](diagramas/) — contexto e contêineres

---

## Build Local (Desenvolvedores)

### CLI Runner (Go)

```bash
cd projetos/assinatura
go mod download
go build -o bin/assinatura ./cmd/assinatura
./bin/assinatura version
```

### Assinador (Java)

```bash
cd projetos/assinador-java
mvn clean package
java -jar target/assinador.jar
```

### Testes

```bash
# CLI
cd projetos/assinatura && go test ./...

# Assinador
cd projetos/assinador-java && mvn test
```

---

## Contribuindo e ajudando a gente com esse projeto 

1. Crie uma issue usando os [templates disponíveis](../../issues/new/choose)
2. Siga a [Definition of Ready e Definition of Done](docs/03-plano-implementacao.md#2-definições-de-ready-dor-e-done-dod)
3. Use [Conventional Commits](https://www.conventionalcommits.org/) nas mensagens
4. Abra um Pull Request linkando a issue correspondente

---

## Contato

- **Disciplina:** Implementação e Integração (2026-01)
- **Professor:** Dr. Fabio Lucena — UFG
- **Equipe:** Thâmara e Jannderson

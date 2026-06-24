# Sistema Runner — Design

> **Documento:** Design da Arquitetura
> **Disciplina:** Implementação e Integração (2026-01)
> **Versão:** 1.0
> **Relacionado:** [01-especificacao.md](./01-especificacao.md)

---

## 1. Visão Geral da Arquitetura

| Contêiner | Tecnologia | Distribuição | Responsabilidade |
|-----------|-----------|--------------|------------------|
| **CLI Runner** | Go 1.22+ | Binário pré-compilado | Interface com usuário, orquestração |
| **Assinador** | Java 21+ | `.jar` (uberjar) | Validação FHIR e simulação de assinaturas |
| **Simulador HubSaúde** | Java (externo) | `.jar` externo | Sistema gerenciado (fora do escopo) |

### 1.1. Princípios Arquiteturais

1. **Separação de responsabilidades:** CLI não implementa regras de negócio de assinatura
2. **Stateless onde possível:** Lógica de negócio é stateless
3. **Falha explícita:** Erros como HTTP status codes + JSON estruturado
4. **Configurabilidade mínima:** Defaults sensatos, override via flags/env vars
5. **Distribuição simples:** Binário único por plataforma

## 2. Diagramas C4

### 2.1. Nível 1 — Contexto

                ┌──────────────────────────┐
                │      Sistema Runner       │
                └────────────┬─────────────┘
                             │
    ┌────────────────────────┼────────────────────────┐
    ▼                        ▼                        ▼
    ──────────────┐ ┌──────────────┐ ┌──────────────┐ │ Usuário │ │ Dispositivo │ │ Simulador │ │ (Ator) │ │ Assinatura │ │ HubSaúde │ └──────────────┘ └──────────────┘ └──────────────┘

text


### 2.2. Nível 2 — Contêineres
┌──────────┐ CLI ┌──────────────────┐ │ Usuário │ ────────▶ │ CLI Runner │ └──────────┘ └────────┬─────────┘ │ ┌──────────────────┼──────────────────┐ ▼ ▼ ▼ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ │ Assinador │ │ Assinador │ │ Simulador │ │ (modo CLI) │ │ (modo HTTP) │ │ HubSaúde │ └──────┬───────┘ └──────┬───────┘ └──────┬───────┘ │ PKCS#11 │ │ ▼ ▼ ▼ ┌──────────────────────────────────┐ │ Dispositivo de Assinatura │ │ Digital (token / smart card) │ └──────────────────────────────────┘


## 3. Decisões Arquiteturais (ADRs)

### ADR-001: Linguagem da CLI — Go
- **Decisão:** Go 1.22+ com Cobra
- **Consequência:** Binário único, startup rápido, cross-compile trivial

### ADR-002: Linguagem do Assinador — Java
- **Decisão:** Java 21+ usando `com.sun.net.httpserver`
- **Consequência:** Zero dependências externas, uberjar simples

### ADR-003: Comunicação CLI ↔ Assinador — Híbrida
- **Decisão:** Suportar modo local (CLI) e servidor (HTTP)
- **Consequência:** Cold start pra scripts, warm start pra múltiplas chamadas

### ADR-004: Provisionamento de JDK — Download sob demanda
- **Decisão:** Eclipse Temurin (Adoptium) com versão fixa (JDK 21 LTS)
- **Consequência:** Primeira execução baixa JDK, execuções seguintes são instantâneas

### ADR-005: Distribuição — GoReleaser + GitHub Releases
- **Decisão:** GoReleaser para empacotar binários e gerar checksums
- **Consequência:** Pipeline automatizado, SemVer a partir de tags

### ADR-006: PKCS#11 — Interface exposta, sem integração real
- **Decisão:** Expor interface PKCS#11 como stub
- **Consequência:** Atende critério formal sem implementar hardware real

## 4. Contratos de Interface

### 4.1. CLI Runner

assinatura [subcomando] [flags]

Comandos: sign create Criar assinatura digital simulada sign validate Validar assinatura digital simulada simulator start Iniciar o Simulador HubSaúde simulator stop Parar o Simulador HubSaúde simulator status Exibir status do Simulador version Exibir versão help Exibir ajuda

**Exit codes:**

| Código | Significado |
|--------|-------------|
| 0 | Sucesso |
| 2 | Uso incorreto (argumentos inválidos) |
| 64 | Entrada do usuário inválida (`EX_USAGE`) |
| 65 | Dados de entrada inválidos (`EX_DATAERR`) |
| 70 | Erro interno de software (`EX_SOFTWARE`) |
| 78 | Erro de configuração (`EX_CONFIG`) |
| 130 | Interrompido pelo usuário (Ctrl+C) |

### 4.2. Assinador — API HTTP

**Base URL:** `http://localhost:8080`

#### `GET /health`
```json
{ "status": "UP", "uptime_seconds": 3600, "requests_handled": 142 }

# CLI Runner (`assinatura`)

> Contêiner do Sistema Runner — Interface de linha de comandos multiplataforma.

## 1. Propósito

Este contêiner é a **CLI Runner** do Sistema Runner. É o binário que o usuário final invoca no terminal para criar/validar assinaturas digitais simuladas e gerenciar o Simulador HubSaúde.

**Não implementa regras de negócio de assinatura** — apenas orquestra chamadas ao contêiner Assinador.

## 2. Tecnologias

| Item | Valor |
|------|-------|
| Linguagem | Go 1.22+ |
| Framework CLI | [Cobra](https://cobra.dev/) |
| Distribuição | Binário único pré-compilado |
| Plataformas-alvo | Windows amd64, Linux amd64, macOS amd64 |

## 3. Pré-requisitos

### Para usar (binário)
- Nenhum — o binário é auto-contido

### Para desenvolver
- Go 1.22 ou superior
- Git
- Acesso ao `assinador.jar` (contêiner irmão) para testes integrados

## 4. Como executar localmente

### Modo 1 — Compilar e rodar do código

```bash
# Baixar dependências
go mod download

# Compilar
go build -o bin/assinatura ./cmd/assinatura

# Rodar
./bin/assinatura version

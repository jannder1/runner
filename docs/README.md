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

```bash
# Linux / macOS
sha256sum -c SHA256SUMS

# Windows (PowerShell)
Get-FileHash assinatura-windows-amd64.exe -Algorithm SHA256

# US-INF-03 — Disponibilizar Binários Multiplataforma

> **Snapshot de user story** — a fonte de verdade é a [issue no GitHub](../../issues).

## Metadados

- **ID:** US-INF-03
- **Épico:** `epic:infra-runtime`
- **Componente:** CLI Runner (Go) + CI/CD
- **Sprint:** 1
- **Estimativa:** M (3-5 dias)
- **Responsável:** Colega (CLI) + você (CI/CD)

## Origem na Spec

[`01-especificacao.md` §8.2 — US-INF-03](../01-especificacao.md#us-inf-03--disponibilizar-binários-multiplataforma)

## User Story

**Como** usuário do Sistema Runner
**Quero** baixar uma versão pré-compilada do CLI para minha plataforma (Windows, Linux ou macOS)
**Para** utilizar o sistema imediatamente sem necessidade de compilação

## Critérios de Aceitação (BDD)

```gherkin
Feature: Distribuição multiplataforma via GitHub Releases
  Como usuário do Sistema Runner
  Quero baixar binários pré-compilados
  Para usar o sistema sem compilar código fonte

  Scenario: Release publicado contém binários
    Given que a tag v0.1.0 foi publicada no GitHub
    When eu acesso a página de Releases
    Then existem os assets:
      | assinatura-windows-amd64.exe |
      | assinatura-linux-amd64       |
      | assinatura-darwin-amd64      |
    And existe o asset SHA256SUMS
    And existe o asset SHA256SUMS.sig (assinatura GPG, se configurada)

  Scenario: Verificação de integridade no Linux
    Given que baixei assinatura-linux-amd64 e SHA256SUMS
    When executo "sha256sum -c SHA256SUMS"
    Then a saída inclui "assinatura-linux-amd64: OK"
    And retorna exit code 0

  Scenario: Verificação de integridade no Windows
    Given que baixei assinatura-windows-amd64.exe
    When executo Get-FileHash no PowerShell
    And comparo com o valor em SHA256SUMS
    Then os valores são idênticos

  Scenario: Versão segue SemVer
    Given que estamos na release v0.1.0
    When uma breaking change é introduzida
    Then a próxima release é v1.0.0
    And a documentação lista o que mudou

  Scenario: Release draft criado automaticamente
    Given que fiz push da tag v0.2.0
    When o GitHub Actions executa o workflow de release
    Then um draft release é criado
    And os binários são anexados
    And os checksums são gerados
    And o changelog é gerado a partir de Conventional Commits
```

## Definition of Ready

- [x] Vinculada a um épico
- [x] User story no formato correto
- [x] Critérios de aceitação em Gherkin
- [x] Estimativa definida (M)
- [ ] Tasks técnicas linkadas
- [ ] Dúvidas com professor resolvidas

## Definition of Done

- [ ] `.goreleaser.yaml` configurado para builds multiplataforma
- [ ] Workflow `.github/workflows/release.yml` funcionando
- [ ] Workflow `.github/workflows/ci.yml` rodando build + test
- [ ] Primeiro release `v0.1.0` publicado com binários + SHA256SUMS
- [ ] Download de binário e verificação de checksum funcionando
- [ ] Documentação de release no README

## Tasks Técnicas Vinculadas

- [ ] TASK-INF-01 — Configurar `.goreleaser.yaml`
- [ ] TASK-INF-02 — Configurar GitHub Actions de release
- [ ] TASK-INF-03 — Configurar GitHub Actions de CI
- [ ] TASK-INF-04 — Publicar primeiro release `v0.1.0`
- [ ] TASK-INF-05 — Validar checksums SHA256 no fluxo

## Dependências

- **Bloqueada por:** US-CLI-01 (precisa ter CLI funcional pra empacotar)
- **Bloqueia:** Nenhuma (pode começar em paralelo com US-CLI-01 pra preparar CI)

## Notas

- Usar [GoReleaser](https://goreleaser.com/) — é o padrão de fato pra releases Go
- SemVer: `MAJOR.MINOR.PATCH` ([semver.org](https://semver.org/))
- Targets: `windows/amd64`, `linux/amd64`, `darwin/amd64`
- Arquivo de release inclui `README.md`, `LICENSE`, `SHA256SUMS`
- Tags seguem padrão `vX.Y.Z` (com prefixo `v` por convenção Go)
- Conventional Commits + GoReleaser geram changelog automático
- Considerar assinatura GPG dos releases (opcional, recomendado)

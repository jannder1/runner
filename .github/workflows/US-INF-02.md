# US-INF-02 — Provisionar JDK Automaticamente

> **Snapshot de user story** — a fonte de verdade é a [issue no GitHub](../../issues).

## Metadados

- **ID:** US-INF-02
- **Épico:** `epic:infra-runtime`
- **Componente:** CLI Runner (Go)
- **Sprint:** 2
- **Estimativa:** M (3-5 dias)
- **Responsável:** Colega (desenvolvedor CLI)

## Origem na Spec

[`01-especificacao.md` §8.2 — US-INF-02](../01-especificacao.md#us-inf-02--provisionar-jdk-automaticamente)

## User Story

**Como** usuário do Sistema Runner
**Quero** que o sistema baixe e configure automaticamente o JDK necessário quando este não estiver disponível
**Para** utilizar o Assinador e o Simulador sem precisar instalar ou configurar o Java manualmente

## Critérios de Aceitação (BDD)

```gherkin
Feature: Provisionamento automático de JDK
  Como usuário do Sistema Runner
  Quero que o JDK seja baixado automaticamente
  Para não precisar instalar/configurar Java manualmente

  Scenario: JDK ausente em Linux amd64
    Given que JAVA_HOME não está definido
    And o comando "java" não está no PATH
    And o sistema é Linux amd64
    When eu executo "assinatura version" (ou qualquer comando)
    Then o sistema detecta ausência de JDK
    And baixa JDK 21 LTS do Eclipse Temurin (linux-amd64)
    And extrai em ~/.runner/jdk/
    And configura JAVA_HOME para uso interno
    And exibe mensagem "JDK provisionado com sucesso em ~/.runner/jdk/"
    And o comando original continua executando normalmente

  Scenario: JDK presente via JAVA_HOME
    Given que JAVA_HOME=/usr/lib/jvm/java-21-openjdk aponta para JDK 21+
    When eu executo "assinatura version"
    Then o sistema usa o JDK existente
    And não faz download

  Scenario: JDK presente via PATH
    Given que JAVA_HOME não está definido
    And o comando "java -version" retorna JDK 21+
    When eu executo "assinatura version"
    Then o sistema usa o JDK encontrado no PATH
    And não faz download

  Scenario: JDK já provisionado pelo Runner
    Given que ~/.runner/jdk/ contém JDK 21+ extraído
    When eu executo "assinatura version"
    Then o sistema reusa o JDK provisionado
    And não faz download
    And termina em < 100ms (cache hit)

  Scenario: Plataforma não suportada (Windows ARM)
    Given que a plataforma é Windows ARM64
    When eu executo "assinatura version"
    Then o sistema exibe mensagem "Plataforma windows-arm64 não suportada. Plataformas suportadas: windows-amd64, linux-amd64, darwin-amd64"
    And retorna exit code 78 (EX_CONFIG)

  Scenario: Falha de download (offline)
    Given que o JDK não está presente
    And não há conectividade de rede
    When eu executo "assinatura version"
    Then o sistema exibe mensagem "Falha ao baixar JDK. Verifique sua conexão ou use --offline se o JDK já está instalado"
    And retorna exit code 70 (EX_SOFTWARE)

  Scenario: Usar JDK do sistema com flag --offline
    Given que JAVA_HOME aponta para JDK 21+
    When eu executo "assinatura --offline version"
    Then o sistema usa o JDK do sistema sem tentar download
```

## Definition of Ready

- [x] Vinculada a um épico
- [x] User story no formato correto
- [x] Critérios de aceitação em Gherkin
- [x] Estimativa definida (M)
- [ ] Tasks técnicas linkadas
- [ ] Dúvidas com professor resolvidas (qual distribuição de JDK?)

## Definition of Done

- [ ] Detecção de JDK presente (JAVA_HOME, PATH, cache)
- [ ] Download de JDK do Eclipse Temurin via API oficial
- [ ] Extração e configuração automática
- [ ] Cache em `~/.runner/jdk/`
- [ ] Suporte a windows-amd64, linux-amd64, darwin-amd64
- [ ] Mensagem clara de erro para plataforma não suportada
- [ ] Testes com mock de download
- [ ] Build do CI verde

## Tasks Técnicas Vinculadas

- [ ] TASK-INF-09 — Detectar JDK presente (JAVA_HOME, PATH)
- [ ] TASK-INF-10 — Implementar download de JDK (Temurin API)
- [ ] TASK-INF-11 — Extrair e configurar JDK baixado
- [ ] TASK-INF-12 — Implementar cache local de JDK
- [ ] TASK-INF-13 — Testes de integração com mock
- [ ] TASK-INF-14 — Documentar comportamento no README

## Dependências

- **Bloqueada por:** Nenhuma (pode começar em paralelo com Sprint 1)
- **Bloqueia:** US-INF-01 (Simulador precisa de JDK)

## Notas

- **Decisão pendente:** Usar Eclipse Temurin (Adoptium) API — é gratuita, oficial, e tem API REST para download.
  - Endpoint: `https://api.adoptium.net/v3/assets/feature_releases/21/ga`
- JDK 21 LTS é a escolha: suporte longo, padrão atual.
- Diretório de cache: `~/.runner/jdk/` (em Windows: `%USERPROFILE%\.runner\jdk\`)
- Considerar checksum SHA256 do download (Temurin fornece)
- Flag `--offline` deve estar disponível em todos os comandos (não só version)

# US-INF-01 — Gerenciar Ciclo de Vida do Simulador do HubSaúde

> **Snapshot de user story** — a fonte de verdade é a [issue no GitHub](../../issues).

## Metadados

- **ID:** US-INF-01
- **Épico:** `epic:infra-runtime`
- **Componente:** CLI Runner (Go) + Simulador (externo)
- **Sprint:** 2
- **Estimativa:** M (3-5 dias)
- **Responsável:** Colega (desenvolvedor CLI)

## Origem na Spec

[`01-especificacao.md` §8.2 — US-INF-01](../01-especificacao.md#us-inf-01--gerenciar-ciclo-de-vida-do-simulador-do-hubsaúde)

## User Story

**Como** usuário do Sistema Runner
**Quero** iniciar, parar e monitorar o Simulador do HubSaúde (`simulador.jar`) através do CLI
**Para** gerenciar o ciclo de vida do Simulador sem conhecer os comandos Java subjacentes

## Critérios de Aceitação (BDD)

```gherkin
Feature: CLI gerencia ciclo de vida do Simulador HubSaúde
  Como usuário do Sistema Runner
  Quero iniciar, parar e monitorar o Simulador
  Para gerenciar seu ciclo de vida sem conhecer comandos Java

  Scenario: Iniciar o simulador
    Given que o simulador.jar está disponível no caminho configurado
    And o simulador não está rodando
    When eu executo "assinatura simulator start"
    Then o CLI inicia o processo java -jar simulador.jar em background
    And exibe mensagem "Simulador iniciado (PID: 12345)"
    And retorna exit code 0

  Scenario: Tentar iniciar simulador já em execução
    Given que o simulador está rodando com PID 12345
    When eu executo "assinatura simulator start"
    Then o CLI exibe mensagem "Simulador já está em execução (PID: 12345)"
    And retorna exit code 0 (não é erro)

  Scenario: Parar o simulador em execução
    Given que o simulador está rodando com PID 12345
    When eu executo "assinatura simulator stop"
    Then o CLI envia sinal SIGTERM para o processo
    And aguarda até 10 segundos pelo shutdown gracioso
    And exibe mensagem "Simulador parado"
    And retorna exit code 0

  Scenario: Parar simulador que não está rodando
    Given que o simulador não está rodando
    When eu executo "assinatura simulator stop"
    Then o CLI exibe mensagem "Simulador não está em execução"
    And retorna exit code 0

  Scenario: Consultar status (rodando)
    Given que o simulador está rodando com PID 12345
    When eu executo "assinatura simulator status"
    Then o CLI exibe:
      """
      Status: running
      PID:    12345
      Uptime: 2h 15m
      """

  Scenario: Consultar status (parado)
    Given que o simulador não está rodando
    When eu executo "assinatura simulator status"
    Then o CLI exibe:
      """
      Status: stopped
      """
```

## Definition of Ready

- [x] Vinculada a um épico
- [x] User story no formato correto
- [x] Critérios de aceitação em Gherkin
- [x] Estimativa definida (M)
- [ ] Tasks técnicas linkadas
- [ ] Dúvidas com professor resolvidas

## Definition of Done

- [ ] Comandos `simulator start`, `stop`, `status` implementados
- [ ] Gerenciamento de PID file (`.runner/simulator.pid`)
- [ ] Sinal SIGTERM enviado corretamente
- [ ] Timeout de shutdown gracioso configurável
- [ ] Testes unitários dos comandos
- [ ] Testes BDD cobrindo cenários
- [ ] Build do CI verde

## Tasks Técnicas Vinculadas

- [ ] TASK-INF-06 — Adicionar comando `simulator start`
- [ ] TASK-INF-07 — Adicionar comando `simulator stop`
- [ ] TASK-INF-08 — Adicionar comando `simulator status`
- [ ] TASK-INF-15 — Implementar gerenciamento de PID file
- [ ] TASK-INF-16 — Implementar shutdown gracioso com timeout
- [ ] TASK-INF-17 — Testes unitários dos comandos simulator
- [ ] TASK-INF-18 — Testes BDD do ciclo de vida

## Dependências

- **Bloqueada por:** US-INF-02 (precisa do JDK provisionado pra invocar `java -jar simulador.jar`)
- **Bloqueia:** Nenhuma

## Notas

- O `simulador.jar` é externo — não é escopo deste projeto
- Usar PID file em `.runner/simulator.pid` para tracking
- SIGTERM com timeout; SIGKILL como fallback
- Considerar flag `--simulator-path` para permitir customizar o caminho do jar

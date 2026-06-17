# US-CLI-01 — Invocar Assinador via CLI

> **Snapshot de user story** — a fonte de verdade é a [issue no GitHub](../../issues).
> Após criar a issue, este arquivo pode ficar desatualizado.

## Metadados

- **ID:** US-CLI-01
- **Épico:** `epic:cli-assinador`
- **Componente:** CLI Runner (Go)
- **Sprint:** 1
- **Estimativa:** M (3-5 dias)
- **Responsável:** Colega (desenvolvedor CLI)

## Origem na Spec

[`01-especificacao.md` §8.2 — US-CLI-01](../01-especificacao.md#us-cli-01--invocar-assinador-via-cli)

## User Story

**Como** usuário do Sistema Runner
**Quero** executar comandos de assinatura digital através da linha de comandos
**Para** invocar a aplicação `assinador.jar` sem conhecer os detalhes técnicos de configuração Java

## Critérios de Aceitação (BDD)

```gherkin
Feature: Invocar Assinador via CLI
  Como usuário do Sistema Runner
  Quero executar comandos de assinatura digital
  Para invocar o Assinador sem conhecer detalhes de configuração Java

  Scenario: Criar assinatura com parâmetros válidos
    Given que o Assinador está disponível (em modo servidor ou local)
    When eu executo "assinatura sign create --input doc.pdf --cert-id cert-001"
    Then o sistema invoca o Assinador com os parâmetros corretos
    And exibe a assinatura simulada retornada

  Scenario: Validar assinatura com parâmetros válidos
    Given que o Assinador está disponível
    When eu executo "assinatura sign validate --signature 'RUNNER_SIM_SIG_...'"
    Then o sistema invoca o Assinador com os parâmetros
    And exibe o resultado da validação (válida/inválida)

  Scenario: Comando sem subcomando reconhecido
    When eu executo "assinatura foo"
    Then o sistema exibe mensagem de erro clara
    And sugere os comandos disponíveis via "assinatura --help"
    And retorna exit code 2 (uso incorreto)

  Scenario: Argumentos obrigatórios ausentes
    When eu executo "assinatura sign create" sem --input
    Then o sistema exibe mensagem "missing required flag: --input"
    And retorna exit code 64 (EX_USAGE)

  Scenario: Erro de comunicação com Assinador
    Given que o Assinador não está rodando
    When eu executo "assinatura sign create --input doc.pdf"
    Then o sistema tenta iniciar o Assinador automaticamente
    And se falhar, exibe mensagem clara de erro
    And retorna exit code 70 (EX_SOFTWARE)
```

## Definition of Ready

- [x] Vinculada a um épico
- [x] User story no formato correto
- [x] Critérios de aceitação em Gherkin
- [x] Estimativa definida (M)
- [ ] Tasks técnicas linkadas
- [ ] Dúvidas com professor resolvidas

## Definition of Done

- [ ] Comandos `sign create` e `sign validate` implementados
- [ ] Auto-start do Assinador funcionando
- [ ] Testes unitários para os comandos
- [ ] Testes BDD cobrindo todos os cenários
- [ ] Build do CI verde
- [ ] Documentação `--help` completa

## Tasks Técnicas Vinculadas

- [ ] TASK-CLI-01 — Adicionar comando `sign create`
- [ ] TASK-CLI-02 — Adicionar comando `sign validate`
- [ ] TASK-CLI-03 — Implementar cliente HTTP do Assinador
- [ ] TASK-CLI-04 — Implementar auto-start do Assinador
- [ ] TASK-CLI-05 — Tratamento de erros com exit codes
- [ ] TASK-CLI-06 — Testes unitários dos comandos
- [ ] TASK-CLI-07 — Testes BDD (Gherkin)

## Dependências

- **Bloqueada por:** US-AS-01 (Assinador precisa estar pronto)
- **Bloqueia:** Nenhuma

## Notas

- O colega do CLI pode começar o esqueleto dos comandos em paralelo com a finalização do Assinador, usando mocks
- Usar [Cobra](https://cobra.dev/) para parsing de comandos
- Definir exit codes consistentes com a convenção `sysexits.h` (ver [02-design.md §4.1](../02-design.md#41-cli-runner--interface-de-linha-de-comandos))

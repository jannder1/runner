---
name: User Story
about: Criar uma user story no formato BDD com critérios de aceitação
title: "[US-XXX] "
labels: ["type:story", "needs-triage"]
assignees: ""
---

## Origem

- **Épico:** <!-- epic:cli-assinador | epic:assinador-core | epic:infra-runtime -->
- **Spec:** <!-- link para a seção da especificação -->
- **Sprint:** <!-- 1, 2 ou 3 -->

## User Story

**Como** [persona]
**Quero** [ação/funcionalidade]
**Para** [benefício/valor]

## Critérios de Aceitação (BDD/Gherkin)

```gherkin
Feature: <nome da feature>

  Scenario: <cenário feliz>
    Given <pré-condição>
    When <ação>
    Then <resultado esperado>
    And <resultado adicional>

  Scenario: <cenário de erro>
    Given <pré-condição>
    When <ação>
    Then <resultado esperado de erro>

  Scenario: <cenario de borda>
    Given <pré-condição>
    When <ação>
    Then <resultado esperado>
```

## Definition of Ready (DoR)

- [ ] Vinculada a um épico (label `epic:*`)
- [ ] User story no formato "Como... Quero... Para..."
- [ ] Critérios de aceitação em Gherkin
- [ ] Estimativa definida (S / M / L)
- [ ] Ao menos 1 task técnica linkada
- [ ] Dúvidas com o professor resolvidas

## Definition of Done (DoD)

- [ ] Tasks linkadas mergeadas via PR
- [ ] Critérios de aceitação cobertos por testes BDD
- [ ] Cobertura de testes ≥ 70%
- [ ] Build do CI verde
- [ ] Documentação atualizada (se aplicável)
- [ ] Code review aprovado

## Estimativa

<!-- S (~1-2 dias) | M (~3-5 dias) | L (~1-2 semanas) -->

## Tasks Técnicas

<!-- Linkar as tasks técnicas que implementam esta story -->
- [ ] #TASK-XX — Título
- [ ] #TASK-YY — Título

## Dependências

<!-- Outras stories ou sistemas externos que precisam existir antes -->
- Depende de: US-XXX
- Bloqueia: US-YYY

## Notas e Referências

<!-- Links, decisões, prints, etc. -->

# US-AS-01 — Simular Assinatura Digital com Validação de Parâmetros

> **Snapshot de user story** — a fonte de verdade é a [issue no GitHub](../../issues).

## Metadados

- **ID:** US-AS-01
- **Épico:** `epic:assinador-core`
- **Componente:** Assinador (Java)
- **Sprint:** 1
- **Estimativa:** M (3-5 dias)
- **Responsável:** Você + colega

## Origem na Spec

[`01-especificacao.md` §8.2 — US-AS-01](../01-especificacao.md#us-as-01--simular-assinatura-digital-com-validação-de-parâmetros)

## User Story

**Como** usuário do Sistema Runner
**Quero** que o Assinador valide rigorosamente os parâmetros de entrada antes de simular uma operação
**Para** receber feedback imediato sobre erros de parâmetros, garantindo que apenas requisições bem formadas sejam processadas

## Critérios de Aceitação (BDD)

```gherkin
Feature: Assinador valida parâmetros antes de simular
  Como integrador do Assinador
  Quero que o assinador.jar valide parâmetros FHIR
  Para que requisições mal formadas sejam rejeitadas com mensagem clara

  Scenario: Criar assinatura com parâmetros válidos
    Given um payload JSON com documentContent, practitionerId e systemOid válidos
    When o Assinador recebe requisição POST /sign
    Then retorna HTTP 200 com JSON de SignatureResult
    And o campo signatureValue começa com "RUNNER_SIM_SIG_"

  Scenario: Validação com practitionerId inválido
    Given um payload JSON com practitionerId "USER_001" (uppercase, não permitido)
    When o Assinador recebe requisição POST /sign
    Then retorna HTTP 400 com mensagem "Invalid Practitioner ID (FHIR standard)"
    And o JSON inclui o campo "field": "practitionerId"

  Scenario: Validação com systemOid sem prefixo
    Given um payload JSON com systemOid "2.16.840.1.113883.4.1" (sem prefixo urn:oid:)
    When o Assinador recebe requisição POST /sign
    Then retorna HTTP 400 com mensagem "Invalid System OID format"

  Scenario: Validação com documentContent ausente
    Given um payload JSON sem o campo documentContent
    When o Assinador recebe requisição POST /sign
    Then retorna HTTP 400 com mensagem "Document content is required"

  Scenario: Validar assinatura com signature válida
    Given um payload JSON com signature "RUNNER_SIM_SIG_550e8400..."
    When o Assinador recebe requisição POST /validate
    Then retorna HTTP 200 com {"isValid": true}

  Scenario: Validar assinatura com signature inválida
    Given um payload JSON com signature "INVALID_SIG_123"
    When o Assinador recebe requisição POST /validate
    Then retorna HTTP 200 com {"isValid": false}

  Scenario: Método HTTP não permitido
    When eu executo GET /sign
    Then o Assinador retorna HTTP 405 (Method Not Allowed)
```

## Definition of Ready

- [x] Vinculada a um épico
- [x] User story no formato correto
- [x] Critérios de aceitação em Gherkin
- [x] Estimativa definida (M)
- [ ] Tasks técnicas linkadas
- [ ] Dúvidas com professor resolvidas (decisão sobre PKCS#11 pendente)

## Definition of Done

- [ ] Validação FHIR completa (regex practitionerId, prefixo systemOid, obrigatoriedade)
- [ ] Resposta JSON estruturada (não `toString()` de record)
- [ ] Leitura de practitionerId/systemOid do body HTTP (não hardcoded)
- [ ] Validação de método HTTP (405 para métodos não permitidos)
- [ ] Testes unitários para cada validador
- [ ] Testes BDD cobrindo todos os cenários
- [ ] Uberjar (`assinador.jar`) gerado via `maven-shade`
- [ ] Build do CI verde

## Tasks Técnicas Vinculadas

- [ ] TASK-AS-01 — Refatorar resposta para JSON estruturado
- [ ] TASK-AS-02 — Ler practitionerId/systemOid do body HTTP
- [ ] TASK-AS-03 — Validar método HTTP (405)
- [ ] TASK-AS-04 — Criar classe FhirValidator separada
- [ ] TASK-AS-05 — Implementar POST /validate
- [ ] TASK-AS-06 — Testes unitários (JUnit 5)
- [ ] TASK-AS-07 — Configurar Maven Surefire
- [ ] TASK-AS-08 — Cucumber-JVM + features
- [ ] TASK-AS-09 — Plugin maven-shade para uberjar
- [ ] TASK-AS-10 — Documentar uso no README

## Dependências

- **Bloqueada por:** Nenhuma (pode começar)
- **Bloqueia:** US-CLI-01 (CLI precisa do Assinador rodando)

## Notas

- ⚠️ **Decisão pendente com professor:** O que fazer com a menção a PKCS#11 no critério "suportar interação com dispositivo criptográfico via interface PKCS#11"? Minha sugestão: implementar interface (classe `Pkcs11Interface`) mas sem provider real, documentar explicitamente como stub.
- Basear validações na [especificação FHIR oficial](https://fhir.saude.go.gov.br/r4/seguranca/caso-de-uso-criar-assinatura.html)
- Considerar usar Jackson para serialização JSON (dependência leve, padrão de mercado)

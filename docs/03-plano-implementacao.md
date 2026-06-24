# Sistema Runner — Plano de Implementação

> **Documento:** Plano de Implementação
> **Disciplina:** Implementação e Integração (2026-01)
> **Versão:** 1.0
> **Status:** Plano preliminar
> **Relacionado:** [`01-especificacao.md`](./01-especificacao.md) · [`02-design.md`](./02-design.md)

Este documento organiza a implementação do Sistema Runner em **sprints**, com base nos épicos e user stories definidos na especificação. Foi refatorado a partir do plano revisitado v2 (ver `conversations.md` da disciplina).

---

## 1. Épicos

| ID | Épico | Componente | Responsável sugerido |
|----|-------|------------|---------------------|
| `epic:cli-assinador` | CLI para invocação do Assinador | CLI Runner | Colega (desenvolvedor CLI) |
| `epic:assinador-core` | Lógica Java do Assinador | Assinador | Você + colega |
| `epic:infra-runtime` | JDK, Simulador, build e releases | CLI Runner + CI | Colega (CLI) + você (CI/docs) |

---

## 2. Definições de Ready (DoR) e Done (DoD)

Aplicáveis a **todas** as user stories e tasks técnicas.

### 2.1. Definition of Ready (DoR)

Uma story só entra na sprint quando:

- [ ] Está vinculada a um épico (via label)
- [ ] Tem user story no formato "Como... Quero... Para..."
- [ ] Tem critérios de aceitação escritos em Gherkin (Given/When/Then)
- [ ] Tem estimativa (S/M/L ou pontos de story)
- [ ] Tem ao menos 1 task técnica linkada
- [ ] Dependências externas foram identificadas (se houver)
- [ ] Dúvidas com o professor/docente foram resolvidas

### 2.2. Definition of Done (DoD)

Uma story só é considerada concluída quando:

- [ ] Todo o código da(s) task(s) linkada(s) foi mergeado via PR
- [ ] Todos os critérios de aceitação têm teste BDD passando
- [ ] Cobertura de testes ≥ 70% nas classes novas
- [ ] Build do CI passa (build + test)
- [ ] Documentação de usuário atualizada (se aplicável)
- [ ] Mensagens de commit seguem Conventional Commits
- [ ] Sem `TODO` ou `FIXME` no código de produção
- [ ] Code review aprovado por pelo menos 1 membro da equipe

---

## 3. Sprint 1 — Fundação e Assinador (2 semanas)

**Objetivo da sprint:** ter o Assinador funcional (modo local e servidor) com testes BDD passando, e o esqueleto da CLI Go com comandos básicos.

### 3.1. Sprint Backlog

| Tipo | ID | Título | Épico | Estimativa | Responsável |
|------|-----|--------|-------|------------|-------------|
| Story | US-AS-01 | Simular Assinatura Digital com Validação de Parâmetros | `epic:assinador-core` | M | Você + colega |
| Story | US-CLI-01 | Invocar Assinador via CLI | `epic:cli-assinador` | M | Colega |
| Story | US-INF-03 | Disponibilizar Binários Multiplataforma | `epic:infra-runtime` | M | Colega |

### 3.2. Tasks Técnicas — Épico `assinador-core`

- [ ] **TASK-AS-01**: Refatorar `Main.java` para retornar JSON estruturado (substituir `result.toString()`)
- [ ] **TASK-AS-02**: Implementar leitura de `practitionerId` e `systemOid` do body HTTP no `/sign` (hoje hardcoded)
- [ ] **TASK-AS-03**: Adicionar validação de método HTTP (responder 405 para métodos não permitidos)
- [ ] **TASK-AS-04**: Criar classe `FhirValidator` separada com regras de validação
- [ ] **TASK-AS-05**: Implementar `POST /validate` (atualmente só `/sign` existe)
- [ ] **TASK-AS-06**: Adicionar testes unitários para `SignatureService` e `FhirValidator` (JUnit 5)
- [ ] **TASK-AS-07**: Configurar Maven Surefire pra rodar testes
- [ ] **TASK-AS-08**: Adicionar dependência Cucumber-JVM + escrever feature `sign_create.feature` e `sign_validate.feature`
- [ ] **TASK-AS-09**: Configurar plugin `maven-shade` para gerar uberjar (`assinador.jar`)
- [ ] **TASK-AS-10**: Documentar como rodar o Assinador no `projetos/assinador-java/README.md`

### 3.3. Tasks Técnicas — Épico `cli-assinador`

- [ ] **TASK-CLI-01**: Adicionar comando `sign create` com flags `--input`, `--cert-id`, `--output`
- [ ] **TASK-CLI-02**: Adicionar comando `sign validate` com flags `--signature`, `--output`
- [ ] **TASK-CLI-03**: Implementar cliente HTTP do Assinador (chamar `localhost:8080`)
- [ ] **TASK-CLI-04**: Implementar auto-start do Assinador em modo servidor (lazy start)
- [ ] **TASK-CLI-05**: Adicionar tratamento de erros com exit codes (64, 65, 70, 78)
- [ ] **TASK-CLI-06**: Adicionar testes unitários para comandos CLI
- [ ] **TASK-CLI-07**: Remover arquivo órfão `RunnerSignatureUtil.java` (decidir destino antes)

### 3.4. Tasks Técnicas — Épico `infra-runtime`

- [ ] **TASK-INF-01**: Configurar `.goreleaser.yaml` para builds multiplataforma
- [ ] **TASK-INF-02**: Configurar GitHub Actions para release automatizado (`.github/workflows/release.yml`)
- [ ] **TASK-INF-03**: Configurar GitHub Actions para CI (`.github/workflows/ci.yml`) — build + test
- [ ] **TASK-INF-04**: Publicar primeiro release `v0.1.0` (MVP)
- [ ] **TASK-INF-05**: Validar checksums SHA256 no fluxo de release

### 3.5. Entregáveis da Sprint 1

- [ ] Assinador funcional em modo local e servidor
- [ ] CLI Runner com `sign create`, `sign validate`, `version`
- [ ] Build local funcionando em pelo menos 1 plataforma
- [ ] GitHub Actions CI verde
- [ ] Primeiro release `v0.1.0` publicado com binários + checksums
- [ ] Cobertura de testes ≥ 70%

---

## 4. Sprint 2 — Simulador e JDK (2 semanas)

**Objetivo da sprint:** completar US-INF-01 (gerenciamento do Simulador) e US-INF-02 (provisionamento de JDK).

### 4.1. Sprint Backlog

| Tipo | ID | Título | Épico | Estimativa | Responsável |
|------|-----|--------|-------|------------|-------------|
| Story | US-INF-01 | Gerenciar Ciclo de Vida do Simulador do HubSaúde | `epic:infra-runtime` | M | Colega |
| Story | US-INF-02 | Provisionar JDK Automaticamente | `epic:infra-runtime` | M | Colega |

### 4.2. Tasks Técnicas — Épico `infra-runtime`

- [ ] **TASK-INF-06**: Adicionar comando `simulator start` ao CLI
- [ ] **TASK-INF-07**: Adicionar comando `simulator stop` ao CLI
- [ ] **TASK-INF-08**: Adicionar comando `simulator status` ao CLI
- [ ] **TASK-INF-09**: Implementar detecção de JDK presente (checar `JAVA_HOME`, `java` no PATH)
- [ ] **TASK-INF-10**: Implementar download de JDK (Eclipse Temurin API) para windows/linux/darwin amd64
- [ ] **TASK-INF-11**: Implementar extração e configuração de JDK baixado
- [ ] **TASK-INF-12**: Adicionar cache de JDK (evitar re-download)
- [ ] **TASK-INF-13**: Adicionar testes de integração para provisionamento (com mock de download)
- [ ] **TASK-INF-14**: Documentar comportamento de provisionamento no `README.md`

### 4.3. Entregáveis da Sprint 2

- [ ] CLI Runner com `simulator start/stop/status` funcionais
- [ ] CLI Runner provisiona JDK automaticamente
- [ ] Testes BDD cobrindo US-INF-01 e US-INF-02
- [ ] Release `v0.2.0` publicado
- [ ] Documentação atualizada

---

## 5. Sprint 3 — Polimento e Documentação (1 semana)

**Objetivo da sprint:** fechar gaps, polir UX, completar documentação.

### 5.1. Backlog

- [ ] Melhorar mensagens de erro do CLI (claras e acionáveis)
- [ ] Adicionar `help` detalhado por comando
- [ ] Criar `Manual de Usuário` (`docs/05-manual-usuario.md`)
- [ ] Criar `Guia de Instalação` (`docs/06-guia-instalacao.md`)
- [ ] Adicionar `Exemplos de Uso` (`docs/07-exemplos-uso.md`)
- [ ] Exportar diagramas C4 para `diagramas/imagens/` (SVG + fonte Mermaid)
- [ ] Revisar todos os READMEs do projeto
- [ ] Configurar `dependabot` ou `renovate` para atualizações
- [ ] Release `v1.0.0` (versão estável)

### 5.2. Entregáveis da Sprint 3

- [ ] Documentação completa de usuário
- [ ] Diagramas C4 exportados
- [ ] CLI com UX polida
- [ ] Release `v1.0.0` estável

---

## 6. Mapa de Dependências

```
Sprint 1                          Sprint 2                      Sprint 3
─────────────────────────────────  ───────────────────────────  ──────────────────────
US-AS-01 (assinador-core)         US-INF-01 (simulator)         Docs
    ↓                                  ↓                            ↓
US-CLI-01 (cli-assinador)         US-INF-02 (jdk)               Polimento
    ↓
US-INF-03 (binários)
```

**Dependências críticas:**
- `US-CLI-01` depende de `US-AS-01` (CLI precisa do Assinador rodando)
- `US-INF-01` depende de `US-INF-02` (CLI precisa de JDK pra invocar simulador.jar)
- `US-INF-03` pode começar em paralelo desde Sprint 1

---

## 7. Riscos e Mitigações

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Java HttpServer não suportar carga/concorrência esperada | Baixa | Médio | Adicionar testes de carga; plano B = Javalin |
| Download de JDK falhar (rede, mirror offline) | Média | Alto | Suportar mirror alternativo; flag `--offline` |
| Cross-compile Go para Windows ter problemas com paths | Média | Baixo | Testar em CI nos 3 SOs |
| Escopo do PKCS#11 gerar ambiguidade com professor | Média | Médio | Documentar explicitamente como stub; alinhar antes de entregar |
| Tempo de compilação do uberjar Java | Baixa | Baixo | Usar profile de release; cache no CI |

---

## 8. Critérios de Aceitação do Trabalho (geral)

Conforme §9 da especificação, o trabalho é considerado "pronto" quando:

- [x] Especificação completa e versionada
- [x] Diagramas C4 (Nível 1 e 2) documentados
- [ ] Código-fonte da CLI Runner implementada e documentada
- [ ] Código-fonte do Assinador implementado e documentado
- [ ] Integração CLI ↔ Assinador funcionando
- [ ] Validação rigorosa de parâmetros (FHIR)
- [ ] Simulação de criação de assinatura
- [ ] Simulação de validação de assinatura
- [ ] Testes unitários, integração e BDD
- [ ] Tratamento de erros com mensagens claras
- [ ] Manual de usuário
- [ ] Documentação técnica de integração
- [ ] Exemplos de uso
- [ ] Guia de instalação
- [ ] Binários pré-compilados (Windows, Linux, macOS amd64)
- [ ] Distribuição via GitHub Releases
- [ ] Checksums SHA256
- [ ] Versionamento SemVer

---

## 9. Anexo A — Histórico de Versões

| Versão | Data | Autor | Mudanças |
|--------|------|-------|----------|
| 1.0 | 2026-06-16 | Equipe | Refatoração do plano revisitado #2 em sprints práticas, com DoR/DoD explícitos |

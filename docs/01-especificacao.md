[01-especificacao.md](https://github.com/user-attachments/files/29272221/01-especificacao.md)

# Sistema Runner — Especificação

> **Documento:** Especificação do Sistema Runner
> **Disciplina:** Implementação e Integração (2026-01)
> **Curso:** Bacharelado em Engenharia de Software
> **Versão:** 1.0 (refatorada)
> **Status:** Aprovada para desenvolvimento
> Link canônico: https://github.com/kyriosdata/runner/blob/<sha>/runner.md

---

## 1. Visão Geral

Este documento define o contexto, o escopo e os requisitos do **Sistema Runner**, trabalho prático da disciplina de Implementação e Integração (2026-01). O objetivo do trabalho é proporcionar aos estudantes a prática de construção de software por meio do desenvolvimento de uma CLI multiplataforma e de uma aplicação Java auxiliar.

## 2. Glossário

| Termo | Significado |
|-------|-------------|
| **CLI** | Command Line Interface (interface de linha de comandos) |
| **CLI Runner** | Aplicação de linha de comandos que o usuário utiliza (também chamada de "aplicação assinatura" na spec original) |
| **Assinador** | Aplicação Java (`assinador.jar`) que simula operações de assinatura digital |
| **Simulador** | Aplicação Java externa (`simulador.jar`) que representa o HubSaúde — **fora do escopo de desenvolvimento** |
| **FHIR** | Fast Healthcare Interoperability Resources — padrão de interoperabilidade em saúde |
| **PKCS#11** | Interface padrão para comunicação com dispositivos criptográficos (tokens, smart cards) |
| **JVM** | Java Virtual Machine |
| **JDK** | Java Development Kit (inclui a JVM) |
| **CLI Runner** | Nome dado ao sistema em desenvolvimento (CLI + lógica de orquestração) |
| **SemVer** | Semantic Versioning — padrão de versionamento `MAJOR.MINOR.PATCH` |
| **C4 Model** | Modelo de documentação de arquitetura em 4 níveis (Contexto, Contêiner, Componente, Código) |
| **BDD** | Behavior Driven Development — desenvolvimento orientado a comportamento |
| **Gherkin** | Linguagem de especificação de cenários BDD (Given/When/Then) |
| **DoR** | Definition of Ready — critérios que uma story deve atender antes de entrar na sprint |
| **DoD** | Definition of Done — critérios que uma story deve atender para ser considerada concluída |
| **Épico** | Tema amplo de negócio que agrupa várias user stories |
| **User Story** | Descrição de uma funcionalidade do ponto de vista do usuário, com critérios de aceitação |

## 3. Contexto e Objetivos

### 3.1. Objetivo Geral

Facilitar o acesso à funcionalidade de execução de aplicações Java via linha de comandos.

### 3.2. Objetivos Específicos

1. Permitir que os usuários executem aplicações Java sem necessidade de conhecer detalhes de configuração ou instalação do ambiente Java. Em particular, as aplicações que fazem parte do Sistema Runner.
2. Fornecer uma interface de linha de comandos (CLI) simples e intuitiva para interação com as aplicações Java, permitindo que os usuários executem comandos específicos para cada aplicação, ocultando a complexidade de configuração ou facilitando o acesso às funcionalidades sem necessidade de conhecimento técnico aprofundado.

## 4. Atores e Sistemas Externos

| Elemento | Tipo | Descrição |
|----------|------|-----------|
| Usuário | Ator | Pessoa que interage com o sistema via linha de comandos |
| Dispositivo de Assinatura Digital | Sistema Externo | Hardware criptográfico (token USB, smart card) que armazena certificados e executa operações de assinatura |
| Simulador do HubSaúde | Sistema Externo | Aplicação Web gerenciada pelo CLI que responde a requisições de terceiros |

### 4.1. Diagrama de Contexto (C4 Nível 1)

```
                    ┌──────────────────────────┐
                    │                          │
                    │      Sistema Runner       │
                    │                          │
                    └────────────┬─────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
              ▼                  ▼                  ▼
      ┌──────────────┐   ┌──────────────┐   ┌─────────────────┐
      │   Usuário    │   │ Dispositivo  │   │  Simulador      │
      │  (Ator)      │   │ Assinatura   │   │  HubSaúde       │
      │              │   │ Digital      │   │  (externo)      │
      └──────────────┘   └──────────────┘   └─────────────────┘
```

## 5. Arquitetura de Contêineres (C4 Nível 2)

### 5.1. Visão Geral

O sistema é composto por dois contêineres principais:

- **CLI Runner** (`assinatura-{plat}-{arch}`): aplicação de linha de comandos multiplataforma, distribuída como binário pré-compilado.
- **Assinador** (`assinador.jar`): aplicação Java que implementa a lógica de validação e simulação de assinaturas digitais.

### 5.2. Diagrama de Contêineres

```
  ┌──────────┐    CLI    ┌──────────────┐  chamada local  ┌──────────────┐
  │ Usuário  │ ────────▶ │  CLI Runner  │ ──────────────▶ │  Assinador   │
  │          │           │  (binário)   │                 │  (.jar)      │
  └──────────┘           └──────────────┘                 └──────┬───────┘
                                  │                              │
                                  │ HTTP (start/stop/status)     │ PKCS#11
                                  ▼                              ▼
                          ┌──────────────┐               ┌──────────────┐
                          │  Simulador   │               │ Dispositivo  │
                          │  HubSaúde    │               │ Criptográfico│
                          │  (externo)   │               │              │
                          └──────────────┘               └──────────────┘
```

### 5.3. Comunicação entre Contêineres

| Origem | Destino | Protocolo | Descrição |
|--------|---------|-----------|-----------|
| Usuário | CLI Runner | CLI (stdin/stdout) | Comandos digitados no terminal |
| Usuário | CLI Runner | CLI (stdin/stdout) | Comandos de gerenciamento do Simulador |
| CLI Runner | Assinador | Chamada local ou HTTP | Invocação direta (cold start) ou requisição HTTP (warm start) |
| Assinador | Dispositivo Criptográfico | PKCS#11 | Interface padrão para tokens e smart cards |
| CLI Runner | Simulador HubSaúde | HTTP | Invoca e monitora o ciclo de vida do simulador |

## 6. Componentes

### 6.1. CLI Runner (aplicação `assinatura`)

**Características:**
- Multiplataforma (Windows, Linux e macOS)
- Interface de linha de comandos (CLI)
- Integra-se com a aplicação `assinador.jar`
- Fornece interface amigável para usuários humanos acessarem funcionalidades de assinatura digital
- Gerencia o ciclo de vida do Simulador HubSaúde
- Provisiona JDK automaticamente quando necessário

**Responsabilidades:**
- Receber comandos do usuário
- Validar consistência sintática dos parâmetros de entrada
- Invocar a aplicação `assinador.jar` com os parâmetros
- Apresentar resultados ao usuário de forma legível
- Iniciar, parar e consultar o status do Simulador
- Detectar ausência de JDK e provisioná-lo automaticamente
- Tratar erros e apresentar mensagens apropriadas

### 6.2. Assinador (aplicação `assinador.jar`)

**Características:**
- Implementada em Java (arquivo `.jar`)
- **Não realiza assinatura digital real** (apenas simula)
- Valida rigorosamente os parâmetros de entrada
- Retorna respostas pré-construídas
- Suporta dois modos de execução:
  - **Modo local (CLI):** invocada diretamente via linha de comandos. Cada execução realiza ciclo completo de inicialização da JVM (cold start). Adequado para execuções esporádicas ou scripts.
  - **Modo servidor (HTTP):** iniciada uma única vez, permanece em execução aguardando requisições. Elimina overhead de inicialização (warm start), oferecendo menor latência e maior throughput para múltiplas requisições.

**Responsabilidades:**
- Validar parâmetros recebidos para operações de criação e validação de assinatura
- Reagir corretamente a falhas (parâmetros inválidos)
- Em caso de sucesso na validação:
  - **Criação:** retornar uma assinatura previamente construída (simulada)
  - **Validação:** retornar indicação de sucesso ou falha no formato esperado
- Garantir que todos os parâmetros estejam corretos antes de processar

## 7. Funcionalidades

### 7.1. Criar Assinatura Digital (simulação)

**Entrada:**
- Parâmetros definidos na especificação FHIR: [caso-de-uso-criar-assinatura](https://fhir.saude.go.gov.br/r4/seguranca/caso-de-uso-criar-assinatura.html)

**Processamento:**
1. Validar todos os parâmetros recebidos
2. Verificar formato e completude dos dados
3. Se válido: retornar assinatura pré-construída
4. Se inválido: retornar mensagem de erro apropriada

**Saída:**
- Sucesso: assinatura digital simulada (pré-construída)
- Falha: mensagem de erro indicando o problema

### 7.2. Validar Assinatura Digital (simulação)

**Entrada:**
- Parâmetros definidos na especificação FHIR: [caso-de-uso-validar-assinatura](https://fhir.saude.go.gov.br/r4/seguranca/caso-de-uso-validar-assinatura.html)

**Processamento:**
1. Validar todos os parâmetros recebidos
2. Verificar formato da assinatura e dados associados
3. Se válido: retornar resultado simulado (sucesso/falha)
4. Se inválido: retornar mensagem de erro apropriada

**Saída:**
- Sucesso: indicação se a assinatura é válida ou inválida (simulado)
- Falha: mensagem de erro indicando o problema

### 7.3. Gerenciar Ciclo de Vida do Simulador

**Comandos suportados:**
- `start`: iniciar o Simulador
- `stop`: parar o Simulador
- `status`: exibir o estado atual (running/stopped)

### 7.4. Provisionamento de JDK

**Comportamento:**
- Detectar presença de JDK no ambiente
- Se ausente: baixar versão compatível com a plataforma
- Configurar JDK para uso interno pelo Assinador e Simulador
- Suporte a Windows (amd64), Linux (amd64) e macOS (amd64)

## 8. Requisitos

### 8.1. Épicos

Os requisitos estão organizados em **3 épicos**:

| Épico | Descrição | User Stories |
|-------|-----------|--------------|
| `epic:cli-assinador` | CLI Runner: invocação do Assinador, parsing de comandos, interface com usuário | US-CLI-01 |
| `epic:assinador-core` | Assinador: validação FHIR, simulação de criação/validação de assinatura | US-AS-01 |
| `epic:infra-runtime` | Provisionamento de JDK, ciclo de vida do Simulador, build e distribuição multiplataforma | US-INF-01, US-INF-02, US-INF-03 |

### 8.2. User Stories

#### US-CLI-01 — Invocar Assinador via CLI

**Como** usuário do Sistema Runner
**Quero** executar comandos de assinatura digital através da linha de comandos
**Para** invocar a aplicação `assinador.jar` sem conhecer os detalhes técnicos de configuração Java

**Critérios de aceitação:**
- O CLI deve aceitar comandos para criação de assinatura
- O CLI deve aceitar comandos para validação de assinatura
- O CLI deve invocar o Assinador com os parâmetros fornecidos
- O CLI deve exibir o resultado da operação de forma legível
- O CLI deve tratar erros do Assinador e apresentar mensagens claras

#### US-AS-01 — Simular Assinatura Digital com Validação de Parâmetros

**Como** usuário do Sistema Runner
**Quero** que o Assinador valide rigorosamente os parâmetros de entrada antes de simular uma operação
**Para** receber feedback imediato sobre erros de parâmetros, garantindo que apenas requisições bem formadas sejam processadas

**Critérios de aceitação:**
- O Assinador deve validar todos os parâmetros conforme especificações FHIR
- O Assinador deve simular criação de assinatura retornando resposta pré-construída quando os parâmetros são válidos
- O Assinador deve simular validação de assinatura retornando resultado pré-determinado
- O Assinador deve suportar interação com dispositivo criptográfico (token/smart card) via interface PKCS#11
- O Assinador deve retornar mensagens de erro claras quando parâmetros forem inválidos

> **Nota:** o item sobre PKCS#11 descreve a interface; a integração real com hardware criptográfico está fora do escopo (ver §10.2).

#### US-INF-01 — Gerenciar Ciclo de Vida do Simulador do HubSaúde

**Como** usuário do Sistema Runner
**Quero** iniciar, parar e monitorar o Simulador do HubSaúde (`simulador.jar`) através do CLI
**Para** gerenciar o ciclo de vida do Simulador sem conhecer os comandos Java subjacentes

**Critérios de aceitação:**
- O CLI deve permitir iniciar o Simulador
- O CLI deve permitir parar o Simulador
- O CLI deve exibir o status atual do Simulador (running/stopped)
- O `simulador.jar` **não faz parte** do escopo de desenvolvimento deste sistema (é sistema externo)

#### US-INF-02 — Provisionar JDK Automaticamente

**Como** usuário do Sistema Runner
**Quero** que o sistema baixe e configure automaticamente o JDK necessário quando este não estiver disponível
**Para** utilizar o Assinador e o Simulador sem precisar instalar ou configurar o Java manualmente

**Critérios de aceitação:**
- O sistema deve detectar se o JDK está presente na máquina
- O sistema deve baixar o JDK compatível quando ausente
- O sistema deve configurar o JDK baixado para uso pelo Assinador e Simulador
- O download deve funcionar nas três plataformas suportadas (Windows/Linux/macOS amd64)
- O JDK já presente deve ser reutilizado sem novo download

#### US-INF-03 — Disponibilizar Binários Multiplataforma

**Como** usuário do Sistema Runner
**Quero** baixar uma versão pré-compilada do CLI para minha plataforma (Windows, Linux ou macOS)
**Para** utilizar o sistema imediatamente sem necessidade de compilação

**Critérios de aceitação:**
- Disponibilizar binário para Windows (amd64): `assinatura-windows-amd64.exe`
- Disponibilizar binário para Linux (amd64): `assinatura-linux-amd64`
- Disponibilizar binário para macOS (amd64): `assinatura-darwin-amd64`
- Distribuir via GitHub Releases
- Incluir checksums SHA256 para verificação de integridade (`SHA256SUMS`)
- Utilizar versionamento semântico (SemVer)

### 8.3. Requisitos Funcionais

#### CLI Runner

| ID | Requisito |
|----|-----------|
| RF-CLI-01 | Deve funcionar em Windows, Linux e macOS (amd64) |
| RF-CLI-02 | Deve fornecer interface via linha de comandos |
| RF-CLI-03 | Deve validar entrada do usuário antes de invocar o Assinador |
| RF-CLI-04 | Deve apresentar resultados de forma legível ao usuário |
| RF-CLI-05 | Deve tratar erros e apresentar mensagens apropriadas |
| RF-CLI-06 | Deve permitir iniciar, parar e consultar o status do Simulador |
| RF-CLI-07 | Deve detectar ausência de JDK e provisioná-lo automaticamente |

#### Assinador

| ID | Requisito |
|----|-----------|
| RF-AS-01 | Deve validar rigorosamente todos os parâmetros de entrada conforme FHIR |
| RF-AS-02 | Deve implementar operação de criação de assinatura (simulada) |
| RF-AS-03 | Deve implementar operação de validação de assinatura (simulada) |
| RF-AS-04 | Deve retornar erros claros quando parâmetros são inválidos |
| RF-AS-05 | Deve expor interface PKCS#11 (sem integração real com hardware — ver §10.2) |

### 8.4. Requisitos Não-Funcionais

#### CLI Runner

| ID | Requisito |
|----|-----------|
| RNF-CLI-01 | Deve ser fácil de instalar e executar (binário único) |
| RNF-CLI-02 | Deve ter documentação clara de uso (help integrado e manual) |
| RNF-CLI-03 | Mensagens de erro devem ser claras e acionáveis |
| RNF-CLI-04 | Deve ter tempo de invocação do Assinador inferior a X ms (a definir) |

#### Assinador

| ID | Requisito |
|----|-----------|
| RNF-AS-01 | Deve ser executável em qualquer sistema com JVM compatível |
| RNF-AS-02 | Deve ter tratamento robusto de erros (sem expor stack traces ao usuário) |
| RNF-AS-03 | Deve retornar resultados em formato estruturado (JSON) |
| RNF-AS-04 | Deve suportar modo local (CLI) e modo servidor (HTTP) |

## 9. Integração entre Aplicações

### 9.1. Fluxo de Criação de Assinatura

```
Usuário → CLI Runner → Assinador → CLI Runner → Usuário
```

1. Usuário executa comando para criar assinatura
2. CLI Runner valida entrada do usuário
3. CLI Runner invoca `assinador.jar` com parâmetros
4. Assinador valida parâmetros
5. Assinador retorna assinatura simulada
6. CLI Runner formata resultado
7. CLI Runner apresenta ao usuário

### 9.2. Fluxo de Validação de Assinatura

```
Usuário → CLI Runner → Assinador → CLI Runner → Usuário
```

1. Usuário executa comando para validar assinatura
2. CLI Runner valida entrada do usuário
3. CLI Runner invoca `assinador.jar` com parâmetros
4. Assinador valida parâmetros
5. Assinador retorna resultado simulado
6. CLI Runner formata resultado
7. CLI Runner apresenta ao usuário

### 9.3. Tratamento de Erros

Em qualquer ponto do fluxo, erros devem ser:
- **Capturados** apropriadamente
- **Propagados** de forma estruturada
- **Apresentados** ao usuário de forma clara
- **Acompanhados** de informação suficiente para correção

## 10. Escopo

### 10.1. O que ESTÁ no Escopo

- ✅ Desenvolvimento da CLI Runner (multiplataforma)
- ✅ Desenvolvimento da aplicação `assinador.jar`
- ✅ Integração entre CLI Runner e `assinador.jar`
- ✅ Validação rigorosa de parâmetros
- ✅ Simulação de criação de assinatura
- ✅ Simulação de validação de assinatura
- ✅ Tratamento de erros e exceções
- ✅ Testes (unitários, integração, BDD)
- ✅ Documentação de uso
- ✅ Provisionamento automático de JDK
- ✅ Gerenciamento do ciclo de vida do Simulador
- ✅ Distribuição multiplataforma via GitHub Releases

### 10.2. O que NÃO ESTÁ no Escopo

- ❌ Implementação real de assinatura digital criptográfica
- ❌ Integração com autoridades certificadoras
- ❌ Armazenamento persistente de assinaturas
- ❌ Interface gráfica (GUI)
- ❌ API web ou serviços REST públicos (além do modo servidor interno do Assinador)
- ❌ Autenticação de usuários
- ❌ Geração real de certificados digitais
- ❌ Desenvolvimento do `simulador.jar` (sistema externo)
- ❌ Integração real com dispositivos PKCS#11 (apenas a interface é exposta)

## 11. Entregáveis

Os seguintes entregáveis devem ser produzidos e disponibilizados no repositório GitHub ao longo da disciplina:

### 11.1. Código-fonte

- **CLI Runner**
  - Implementação completa
  - Compatível com Windows, Linux e macOS (amd64)
  - Código bem documentado
- **Aplicação `assinador.jar`**
  - Implementação em Java
  - Validação completa de parâmetros
  - Simulação das operações de criação e validação

### 11.2. Testes

- Testes unitários
- Testes de integração
- Casos de teste para cenários de erro
- Testes BDD (Gherkin) para critérios de aceitação

### 11.3. Documentação

- Manual de usuário da CLI Runner
- Documentação técnica da integração
- Exemplos de uso
- Guia de instalação
- Especificação (este documento)

### 11.4. Artefatos Executáveis

Binários pré-compilados para as três plataformas suportadas:
- `assinatura-windows-amd64.exe`
- `assinatura-linux-amd64`
- `assinatura-darwin-amd64`

**Distribuição:**
- Via GitHub Releases
- Cada release deve conter checksums (`SHA256SUMS`) para verificação de integridade
- Versionamento semântico (SemVer): `MAJOR.MINOR.PATCH`

## 12. Considerações de Implementação

### 12.1. Simulação

Como o sistema simula operações de assinatura digital:

- **Para criação:** preparar assinaturas de exemplo pré-construídas que podem ser retornadas quando os parâmetros são válidos
- **Para validação:** implementar lógica simples que sempre retorna um resultado pré-determinado (válido/inválido) baseado em critérios simples
- **Foco na validação:** a maior parte do esforço deve estar em validar corretamente os parâmetros de entrada

### 12.2. Padrões de Qualidade

- Código limpo e bem organizado
- Tratamento adequado de exceções
- Testes com boa cobertura
- Documentação clara
- Mensagens de erro úteis
- Aderência a DoR/DoD por user story
- Cobertura de critérios de aceitação via testes BDD (Gherkin)

## 13. Referências

- **Especificações FHIR — Segurança**
  - [Caso de Uso: Criar Assinatura](https://fhir.saude.go.gov.br/r4/seguranca/caso-de-uso-criar-assinatura.html)
  - [Caso de Uso: Validar Assinatura](https://fhir.saude.go.gov.br/r4/seguranca/caso-de-uso-validar-assinatura.html)
- **Modelo C4 para Visualização de Arquitetura**
  - [C4 Model](https://c4model.com/)
  - Nível 1: Diagrama de Contexto
  - Nível 2: Diagrama de Contêiner
- **Boas Práticas de CLI**
  - Mensagens claras e consistentes
  - Tratamento adequado de erros
  - Documentação de help integrada
- **BDD e Gherkin**
  - [Cucumber](https://cucumber.io/)

---

## Anexo A — Rastreabilidade

| Requisito | Épico | User Story | Componente |
|-----------|-------|------------|------------|
| US-CLI-01 (original) | `epic:cli-assinador` | US-CLI-01 | CLI Runner |
| US-02 (original) | `epic:assinador-core` | US-AS-01 | Assinador |
| US-03 (original) | `epic:infra-runtime` | US-INF-01 | CLI Runner |
| US-04 (original) | `epic:infra-runtime` | US-INF-02 | CLI Runner |
| US-05 (original) | `epic:infra-runtime` | US-INF-03 | CLI Runner + Build/CI |

## Anexo B — Histórico de Versões

| Versão | Data | Autor | Mudanças |
|--------|------|-------|----------|
| (original) | — | Professor | Versão inicial fornecida na disciplina |
| 1.0 | 2026-06-16 | Refatoração | Reorganização de seções, correção de numeração, separação de épicos, adição de glossário, rastreabilidade e critérios DoR/DoD |

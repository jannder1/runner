# ADR-005: Auto-shutdown por inatividade com reinício do timer

- **Status:** Aceito
- **Data:** 2026-07-02
- **Autores:** @jannderson, @thamaraprata

## Contexto

Quando a CLI sobe o JAR em modo servidor, esse processo fica em
background consumindo recursos até que o usuário explicitamente o
derrube (`assinatura stop`) ou feche o terminal.

O requisito **E2** do enunciado exige um comportamento específico:
*"Auto-shutdown por inatividade com janela configurável; teste
comprovando que o timer reinicia a cada requisição."*

Restrições:

- O timer deve resetar a cada requisição bem-sucedida (não só a cada
  tick).
- Janela configurável (não hardcoded).
- Shutdown deve ser gracioso (não `kill -9`).
- Implementação deve sobreviver a cenários de race (timer disparando
  enquanto uma requisição está chegando).

## Decisão

A janela de inatividade é configurada em **minutos**, default **30**,
via:

- Flag da CLI: `assinatura start --timeout <minutos>`.
- Variável de ambiente do JAR: `HUBSAUDE_TIMEOUT_MINUTES`.

A implementação usa:

1. **`RequestTimestamp`** (thread-safe, `AtomicLong`): atualizado por
   filtro HTTP a cada requisição bem-sucedida.
2. **`InactivityShutdown`**: thread watchdog que, a cada minuto, compara
   `System.currentTimeMillis() - requestTimestamp.get()` com a janela
   configurada. Se exceder, dispara `System.exit(0)`.
3. **`InactivityFilter`**: filtro HTTP que marca timestamp no início do
   processamento.

O comportamento de "reinício do timer a cada requisição" é portanto
**emergente** da combinação dos três: o filtro sempre escreve o timestamp
agora, então a subtração na próxima tick do watchdog sempre parte do
último instante de atividade.

## Consequências

**Mais fácil:**

- Usuário esquece o servidor ligado? Sem problema, ele se desliga sozinho.
- Recurso liberado automaticamente em ambiente de dev.
- Comportamento idêntico em modo local (sem servidor) é trivial: o
  subprocesso encerra ao final da chamada.

**Mais difícil:**

- Janela de 30min pode ser longa demais para CI (testes esperando 30min).
  Mitigação: configurabilidade via flag/env.
- O watchdog é uma thread a mais — pequena superfície de bug (race entre
  leitura de timestamp e `System.exit`). Coberto por teste dedicado.
- Não distingue "atividade de leitura vs erro" — uma sequência de
  requisições com erro 4xx também reseta o timer. Decisão consciente:
  o cliente está vivo e consumindo; considerar isso "atividade".

## Alternativas consideradas

- **Sem auto-shutdown:** rejeitada por violar E2.
- **Timer via `ScheduledExecutorService` com reschedule explícito:** mais
  complexo, sem ganho real sobre o padrão atual (timestamp + poll).
- **Heartbeat bidirecional CLI ↔ JAR:** rejeitada por adicionar
  protocolo sem benefício claro — a janela já é por inatividade HTTP,
  não por inatividade CLI.

## Referências

- Critério E2 do enunciado: "Auto-shutdown por inatividade com janela
  configurável; teste comprovando que o timer reinicia a cada requisição."
- Código: `projetos/assinador-java/src/main/java/com/hubsaude/assinador/infrastructure/InactivityShutdown.java`,
  `RequestTimestamp.java`, `InactivityFilter.java`.
- Teste a implementar: cenário onde duas requisições espaçadas em `T < janela`
  não devem disparar shutdown.
# ADR-002: Modo servidor como default da CLI

- **Status:** Aceito
- **Data:** 2026-07-02
- **Autores:** @jannderson, @thamaraprata

## Contexto

A CLI `assinatura` oferece dois modos de interação com o assinador:

1. **Modo local:** CLI invoca o JAR como subprocess, captura stdout/stderr,
   propaga exit code.
2. **Modo servidor:** CLI sobe (ou reusa) o JAR em background e fala HTTP.

O requisito **E2** do enunciado é taxativo: *"Modo servidor é o padrão;
modo local deve ser explicitamente ativado."*

Restrição operacional:

- Subir JVM por chamada é caro (~1–3s em cold start), além de exigir
  `java` no PATH.
- O assinador tem múltiplas chamadas encadeadas no mesmo fluxo (criar +
  validar), tornando o modo servidor naturalmente vantajoso.

## Decisão

A CLI opera em **modo servidor por padrão**. Para usar modo local, o
usuário deve invocar explicitamente o subcomando com a flag `--local`
(ou subcomando equivalente, ex.: `assinatura sign --local ...`).

A CLI deve:
1. Verificar se já existe instância viva na porta configurada (ver ADR-003).
2. Se sim, reutilizar (idempotência de start).
3. Se não, iniciar e aguardar health check antes de retornar controle.

## Consequências

**Mais fácil:**

- Múltiplas chamadas (`sign create` seguido de `sign validate`) amortizam o
  custo de subir JVM.
- Fica natural manter estado entre chamadas (cache de certificados, contador
  de requisições, métricas).
- Compatível com o que o usuário espera de CLIs modernas (`docker`,
  `kubectl`, `aws`).

**Mais difícil:**

- Lifecycle do processo servidor vira responsabilidade da CLI: PID file,
  shutdown gracioso, detecção de crash. Coberto em ADR-003 e ADR-005.
- Em ambientes CI / one-shot o modo servidor pode ser overkill — daí a
  necessidade de manter `--local` acessível e documentado.
- Logs do servidor e da CLI podem se misturar no terminal do usuário; exige
  disciplina de separar stdout (resultado) de stderr (diagnóstico), como já
  previsto em E1.

## Alternativas consideradas

- **Modo local como padrão:** rejeitada por violar critério E2 explicitamente.
- **Detecção automática (heurística):** decidir entre local e servidor com
  base em flags ou ambiente. Rejeitada por esconder intenção do usuário e
  dificultar debugging.
- **Subir servidor sempre e nem oferecer modo local:** rejeitada porque E1
  exige invocação local funcional.

## Referências

- Critério E2 do enunciado: "Modo servidor é o padrão; modo local deve ser
  explicitamente ativado."
- Critério E1: invocação local do `assinador.jar`.
- ADR-003 (descoberta de instância viva).
- ADR-005 (auto-shutdown por inatividade).
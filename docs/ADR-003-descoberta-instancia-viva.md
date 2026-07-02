# ADR-003: Descoberta de instância viva (health check real)

- **Status:** Aceito
- **Data:** 2026-07-02
- **Autores:** @jannderson, @thamaraprata

## Contexto

A CLI precisa garantir **idempotência de start**: se o usuário rodar
`assinatura start` duas vezes em sequência, o sistema deve detectar que já
existe instância viva e reutilizá-la, em vez de falhar com "porta ocupada"
ou subir um segundo processo zumbi.

O requisito **E2** do enunciado é explícito: *"detecta instância viva
(não só 'porta ocupada' — health check real) e reutiliza."*

A diferença é sutil mas importante:

- **"Porta ocupada"** pode significar: processo subiu e está aceitando
  conexões; **ou** porta em `TIME_WAIT`/`LISTEN` com processo que travou
  no startup; **ou** outro serviço completamente diferente na mesma porta.
- **"Health check real"** significa: GET no endpoint `/health` (ou similar)
  retornando `200 OK` com payload que identifique o serviço como sendo
  *o nosso*.

## Decisão

A CLI implementa a descoberta em duas etapas, em ordem:

1. **Sondagem TCP** na porta configurada (timeout curto, ~500ms).
2. Se a porta responde, **health check HTTP** `GET /health` com timeout
   curto (~1s) e validação de payload (espera-se JSON contendo
   identificador do serviço, ex.: `{"status":"UP"}`).

Só se ambas as etapas passarem a CLI considera a instância "viva" e
reutiliza. Caso contrário:

- Porta ocupada mas health falhou → log diagnóstico em stderr, encerramento
  com código de saída distinto (ex.: `75` — `EX_TEMPFAIL`).
- Porta livre → seguir para start normal.

## Consequências

**Mais fácil:**

- Reuso correto em workflows iterativos (`assinatura start`, depois
  `assinatura sign create`, depois `assinatura start` de novo).
- Detecção precoce de inconsistência: usuário fica sabendo se a porta
  está tomada por outro processo (mensagem clara).

**Mais difícil:**

- Exige endpoint `/health` documentado e estável no JAR. Já existe
  (`HealthController.java`), mas vira contrato — qualquer mudança precisa
  ser refletida aqui.
- Health check adiciona latência na inicialização (~1s no pior caso).
  Aceitável para CLI desktop; talvez questionável em CI. Mitigação: a
  sondagem TCP falha rápido quando ninguém está escutando.
- Em cenários de race (start concorrente), ainda é possível subir duas
  instâncias. Mitigação adicional: PID file com lock `flock`-style. Fora
  do escopo deste ADR; registrar como follow-up.

## Alternativas consideradas

- **Apenas testar porta ocupada (`bind`):** rejeitada por violar E2
  explicitamente.
- **Apenas health check HTTP (sem TCP):** rejeitada por adicionar latência
  desnecessária quando ninguém está escutando (timeout HTTP completo).
- **Lock file (`/tmp/runner.lock`):** rejeitada por ser frágil em ambientes
  com tmpfs volátil e em Windows (semunlink atômico trivial). Considerada
  para futuro.

## Referências

- Critério E2 do enunciado: "Idempotência de start: detecta instância viva
  (não só 'porta ocupada' — health check real) e reutiliza."
- Código: `projetos/assinador-java/src/main/java/com/hubsaude/assinador/presentation/http/HealthController.java`.
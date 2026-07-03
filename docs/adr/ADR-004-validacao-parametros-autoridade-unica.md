# ADR-004: Validação de parâmetros: autoridade única no JAR

- **Status:** Aceito
- **Data:** 2026-07-02
- **Autores:** @jannderson, @thamaraprata

## Contexto

O fluxo de validação de entrada pode acontecer em dois pontos:

1. Na CLI (Go + Cobra), antes mesmo de invocar o JAR.
2. No JAR (Java + `RequestValidator`), ao receber o payload.

O requisito **E3** do enunciado é claro: *"Feita dentro do assinador.jar
(autoridade única), não replicada no CLI."*

Razões para esta restrição:

- Se o JAR é a fonte de verdade do contrato de entrada, qualquer
  consumidor (CLI atual, futuras integrações HTTP diretas, scripts de
  teste) se beneficia da mesma validação.
- Replicar validação na CLI cria drift inevitável: as regras divergem
  silenciosamente, e o usuário recebe mensagens diferentes para o mesmo
  erro dependendo do caminho.
- O CLI, idealmente, é apenas um *transporte* — não deveria conhecer as
  regras de negócio do payload.

## Decisão

A validação de regras de negócio (formato de `certId`, obrigatoriedade
de campos FHIR, consistência de hashes, tamanho mínimo de payload)
**acontece exclusivamente** no JAR, em `RequestValidator.java`.

A CLI pode fazer apenas:
- **Validação sintática mínima** estritamente necessária para construir
  a chamada: arquivo de input existe? Porta é número inteiro? Flag tem
  valor?
- **Nada além disso.** Nenhuma regra sobre conteúdo do payload.

Quando o JAR detecta erro de validação:
- Retorna HTTP `400 Bad Request` com corpo JSON estruturado contendo
  `code`, `message`, `field`.
- Em modo local, propaga exit code não-zero distinto para erros do
  usuário (ex.: `65` — `EX_DATAERR`) versus erros do sistema
  (ex.: `70` — `EX_SOFTWARE`).

## Consequências

**Mais fácil:**

- Fonte única de verdade. Toda mudança em regras de validação vive num
  único arquivo.
- Mensagens consistentes independente do caminho de invocação.
- Testes unitários de validação ficam concentrados em
  `RequestValidatorTest.java` — cobertura alta com baixo custo.

**Mais difícil:**

- Latência: erros de validação custam um round-trip ao JAR. Aceitável,
  dado que erros são exceções no fluxo normal.
- A CLI fica "burra" quanto a regras — usuários podem achar que a CLI
  poderia ter falhado mais cedo. Mitigação: documentação clara no
  manual do usuário sobre quem é autoridade.

## Alternativas consideradas

- **Validação nos dois lados (defesa em profundidade):** rejeitada por
  introduzir drift. Se for sentida falta de feedback rápido, pode-se
  futuramente espelhar apenas o schema (não as regras).
- **Validação só na CLI, JAR aceita tudo:** rejeitada por criar buraco
  para integrações HTTP diretas.

## Referências

- Critério E3 do enunciado: "Feita dentro do assinador.jar (autoridade
  única), não replicada no CLI."
- Código: `projetos/assinador-java/src/main/java/com/hubsaude/assinador/application/validation/RequestValidator.java`.
- Testes: `projetos/assinador-java/src/test/java/com/hubsaude/assinador/application/validation/RequestValidatorTest.java`.
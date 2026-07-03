# ADR-001: Porta padrão do servidor HTTP

- **Status:** Aceito
- **Data:** 2026-07-02
- **Autores:** @jannderson, @thamaraprata

## Contexto

O assinador (`assinador.jar`) precisa expor endpoints HTTP para que o CLI
consuma localmente (`assinatura sign create`, `assinatura sign validate`).
O requisito **E2** do enunciado explicita que a porta deve ser **configurável**
e que o sistema deve **falhar de forma clara** quando a porta está tomada.

Restrições:

- A porta precisa ser alta o suficiente para não exigir privilégio de
  administrador em Windows/Linux.
- Não pode colidir com serviços comuns em ambiente de desenvolvimento.
- Deve ser trivialmente sobrescrita via CLI sem alterar código.

## Decisão

A porta padrão é **8080**, configurável via flag `--port <N>` passada ao JAR
(e propagada pelo CLI `assinatura start --port <N>`).

Implementação atual: `AssinadorApplication.java` lê `--port` dos argumentos,
valida como inteiro e define `server.port` no Spring Boot.

## Consequências

**Mais fácil:**

- Alinhamento com convenção HTTP alternativa (8080 é a substituta histórica de 80).
- Variável de ambiente `SERVER_PORT` do Spring Boot continua funcional como override.
- Testes locais não exigem privilégios.

**Mais difícil:**

- Em ambientes onde 8080 já é usada por outro serviço (Tomcat dev, Jenkins,
  outra instância do próprio Runner), o sistema precisa detectar e falhar com
  mensagem clara. Trade-off aceito e coberto pelo ADR-003.
- A porta **não** é validada como livre *antes* do `bind()` — depende do
  comportamento do Spring Boot. Trade-off aceito pela simplicidade.

## Alternativas consideradas

- **Porta 0 (efêmera):** delegaria ao SO escolher. Rejeitada porque quebra
  a reprodutibilidade dos testes e a previsibilidade do usuário.
- **Porta 9090:** menos colisão, mas quebra expectativa do usuário acostumado
  com apps Spring. Rejeitada por familiaridade.
- **Porta aleatória entre 10000–60000:** evita colisão mas exige o usuário
  descobrir a porta a cada execução. Rejeitada por fricção.

## Referências

- Critério E2 do enunciado: "Porta padrão configurável; falha clara quando a
  porta está tomada por outro processo."
- Código: `projetos/assinador-java/src/main/java/com/hubsaude/assinador/AssinadorApplication.java`.
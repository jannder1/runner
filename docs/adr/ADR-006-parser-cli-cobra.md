# ADR-006: Parser de CLI: Cobra

- **Status:** Aceito
- **Data:** 2026-07-02
- **Autores:** @jannderson, @thamaraprata

## Contexto

A CLI `assinatura` precisa de um parser de linha de comando que suporte:

- Subcomandos aninhados (`assinatura sign create`, `assinatura simulator start`).
- Flags persistentes (`--verbose`, `--port`) e locais por subcomando.
- Geração automática de `--help` e `--version`.
- Mensagens de erro claras.
- Distribuição como binário único estático (sem dependência externa).

O critério **A** do enunciado menciona "parser de CLI" como exemplo de
decisão que merece ADR.

Restrições adicionais do projeto:

- Equipe com experiência prévia em Go (mas não exclusivamente).
- Build cross-platform para Windows, Linux, macOS via GoReleaser.
- Sem dependência de runtime além do binário (Cobra casa com isso).

## Decisão

Usar [**Cobra**](https://github.com/spf13/cobra) como framework de CLI,
complementado por [**Viper**](https://github.com/spf13/viper) apenas se
configuração via arquivo/env for realmente necessária (hoje não é — todas
configurações entram por flag).

A estrutura segue o padrão recomendado pelo Cobra: um arquivo por
subcomando em `projetos/assinatura/cmd/` (`root.go`, `sign.go`,
`start.go`, `run.go`, etc.), registrados em `rootCmd`.

## Consequências

**Mais fácil:**

- `--help` rico e consistente automaticamente (sem escrever manualmente).
- Anotações de comandos suportam flags, aliases, exemplos, grupos.
- Ecossistema Go maduro: integração com `cobra/doc` para gerar docs,
  com `go-flags` para shims POSIX, com `promptui` se対話 for necessário.
- Onboarding de novos contribuidores é rápido — Cobra é o padrão de
  facto em Go.

**Mais difícil:**

- Cobra adiciona um nível de indireção: `cmd/root.go` registra, e cada
  arquivo de subcomando parece repetitivo. Aceitável.
- Viper (se vier a ser usado) tem fama de ser overkill para projetos
  pequenos. Por isso não o adotamos agora.

## Alternativas consideradas

- **`flag` da stdlib:** rejeitada por exigir implementação manual de
  subcomandos aninhados, `--help` por comando, organização de arquivos.
- **`urfave/cli`:** rejeitada por ser menos idiomática no ecossistema
  Go atual e ter menos exemplos prontos.
- **`kingpin` (gopkg.in/alecthomas/kingpin.v2):** rejeitada por estar
  em modo de manutenção. Cobra é o sucessor natural recomendado pelo
  próprio autor.

## Referências

- Critério A do enunciado: "Decisões registradas (ADRs curtos) onde
  houve escolha não óbvia: ... parser de CLI ou outro."
- Código: `projetos/assinatura/cmd/root.go`, `sign.go`, `start.go`, `run.go`.
- Documentação externa: https://github.com/spf13/cobra.
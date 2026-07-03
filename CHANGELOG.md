# Changelog

Todas as mudanças do são documentadas aqui, essa é a versão mais atualizada. 

O formato segue [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/lang/pt-BR/).

## [Unreleased]

### Added
- 6 Architecture Decision Records (ADR-001 a 006) em `docs/adr/`
- 4 diagramas C4 (C1 contexto, C2 contêineres, C3 componentes JAR e CLI) em `diagramas/c4/`
- `LICENSE` (MIT) na raiz
- `projetos/README.md` com índice real dos subprojetos
- `tests/bdd/features/README.md` com tabela de status das features
- Seção "Onde ficam os arquivos de estado" e "Variáveis de ambiente" no manual do usuário
- Seção "Modos de operação" e "Referência de Comandos" no manual

### Changed
- `README.md` reescrito com comandos reais (`sign --content`, `validate`, `start`, `stop`, `run`)
- `docs/05-manual-usuario.md` reescrito para refletir a CLI real
- `docs/02-design.md` reescrito (v2.0): remove diagramas ASCII corrompidos, corrige stack Java para Spring Boot, remove comandos inexistentes
- `.gitignore` ampliado para incluir `target/` e `bin/`
- `projetos/assinatura/.goreleaser.yaml` corrigido (variável `main.Version` em vez de `main.version`)
- BDD `cli_sign_create.feature` e `cli_sign_validate.feature` reescritos para os comandos reais
- CI: versão do Go corrigida de 1.22 para 1.26.2

### Fixed
- `target/` (Maven) removido do versionamento
- `LICENSE-MIT` renomeado para `LICENSE`
- Pasta `docs/adr/` e `diagramas/c4/` criadas corretamente (eram arquivos vazios)
- Label duplicada `description` em

# Backlog do Sistema Runner

> Este diretório contém **snapshots em markdown** das user stories do projeto.
> A **fonte de verdade operacional** são as [GitHub Issues](../../issues).
> Use estes arquivos apenas como referência rápida ou para exportação.

## Como usar o backlog no GitHub

### 1. Aplicar os labels

```bash
# Instalar gh CLI: https://cli.github.com/
gh auth login
gh label sync -f .github/labels.yml --repo jannder1/runner
```

### 2. Criar as user stories

Para cada arquivo `US-*.md` deste diretório:

1. Abra uma nova issue no GitHub
2. Use o template "User Story"
3. Cole o conteúdo do arquivo
4. Aplique o label `epic:*` correspondente
5. Adicione o label `type:story`
6. Adicione à milestone da sprint (Sprint 1, 2 ou 3)

### 3. Criar as tasks técnicas

Para cada user story:

1. Abra uma issue por task usando o template "Task Técnica"
2. No campo "Vinculada à Story", adicione o número da issue da story (ex: `#42`)
3. Aplique os labels `epic:*` e `type:task`

### 4. Configurar GitHub Projects (opcional)

```bash
# Criar projeto
gh project create --title "Runner - Sprints 2026.1"

# Adicionar issues ao projeto (interativo)
gh project item-add <PROJECT_ID> --owner jannder1 --url <ISSUE_URL>
```

## Estrutura do Diretório

```
04-backlog/
├── README.md                       ← este arquivo
├── US-CLI-01.md                    ← User Story: Invocar Assinador via CLI
├── US-AS-01.md                     ← User Story: Simular Assinatura Digital
├── US-INF-01.md                    ← User Story: Gerenciar Simulador
├── US-INF-02.md                    ← User Story: Provisionar JDK
└── US-INF-03.md                    ← User Story: Binários Multiplataforma
```

## Convenções

- **IDs de User Story:** `US-{COMPONENTE}-{NÚMERO}` (ex: `US-CLI-01`)
- **IDs de Task:** `TASK-{COMPONENTE}-{NÚMERO}` (ex: `TASK-AS-03`)
- **Componentes:** `CLI` (CLI Runner), `AS` (Assinador), `INF` (Infraestrutura)
- **Épicos como labels:** `epic:cli-assinador`, `epic:assinador-core`, `epic:infra-runtime`
- **Tipos como labels:** `type:story`, `type:task`, `type:bug`, `type:docs`, `type:chore`
- **Prioridades como labels:** `priority:high`, `priority:medium`, `priority:low`

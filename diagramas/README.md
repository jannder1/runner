
[diagramas-README.md](https://github.com/user-attachments/files/29616144/diagramas-README.md)
# Diagramas

Diagramas arquiteturais do Sistema Runner, escritos em **Mermaid** (renderiza
diretamente no GitHub, GitLab e VS Code).

## Estrutura

- [`c4/`](c4/) — Modelo C4 (Contexto, Contêineres, Componentes)

## Como visualizar

- **GitHub:** abra qualquer `.md` da pasta; o Mermaid renderiza inline.
- **VS Code:** instale a extensão *Markdown Preview Mermaid Support* e abra
  o preview (`Ctrl+Shift+V`).
- **Online:** cole o bloco ```mermaid``` em https://mermaid.live.

## Como editar

1. Identifique o diagrama afetado.
2. Edite o bloco ```mermaid``` correspondente.
3. Mantenha a **fonte da verdade** em sincronia: se mudar um container,
  atualize o diagrama no mesmo commit em que o código muda.

## Referências cruzadas

Cada diagrama referencia ADRs em [`../adr/`](../adr/) quando retrata
decisões. Alterar uma decisão **exige** atualizar diagrama + ADR.

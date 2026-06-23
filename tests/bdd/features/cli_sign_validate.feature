# language: pt
# User Story: US-CLI-01 — Invocar Assinador via CLI
# Componente: CLI Runner (Go)
# Fonte: docs/04-backlog/US-CLI-01.md

Funcionalidade: CLI Runner — Comando sign validate
  Como usuário do Sistema Runner
  Quero executar o comando "assinatura sign validate"
  Para validar uma assinatura digital simulada

  Contexto:
    Dado que a CLI Runner está instalada e disponível no PATH

  @critical
  Cenário: Validar assinatura válida
    Quando eu executo "assinatura sign validate --signature 'RUNNER_SIM_SIG_550e8400'"
    Então o comando deve terminar com exit code 0
    E a saída deve indicar que a assinatura é válida

  Cenário: Validar assinatura inválida
    Quando eu executo "assinatura sign validate --signature 'INVALID_SIG' --allow-invalid"
    Então o comando deve terminar com exit code 0
    E a saída deve indicar que a assinatura é inválida

  Cenário: Assinatura inválida sem flag --allow-invalid
    Quando eu executo "assinatura sign validate --signature 'INVALID_SIG'"
    Então o comando deve terminar com exit code 65
    E a saída deve indicar que a assinatura é inválida

  Cenário: Flag obrigatória --signature ausente
    Quando eu executo "assinatura sign validate"
    Então o comando deve terminar com exit code 64

  Cenário: --help exibe ajuda
    Quando eu executo "assinatura sign validate --help"
    Então o comando deve terminar com exit code 0
    E a saída deve listar todas as flags

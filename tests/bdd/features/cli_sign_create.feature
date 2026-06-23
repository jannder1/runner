# language: pt
# User Story: US-CLI-01 — Invocar Assinador via CLI
# Componente: CLI Runner (Go)
# Fonte: docs/04-backlog/US-CLI-01.md

Funcionalidade: CLI Runner — Comando sign create
  Como usuário do Sistema Runner
  Quero executar o comando "assinatura sign create"
  Para criar uma assinatura digital simulada

  Contexto:
    Dado que a CLI Runner está instalada e disponível no PATH

  @critical
  Cenário: Criar assinatura com sucesso
    Quando eu executo "assinatura sign create --input documento.txt --cert-id cert-001"
    Então o comando deve terminar com exit code 0
    E a saída deve conter uma assinatura simulada começando com "RUNNER_SIM_SIG_"

  @critical
  Cenário: Falta flag obrigatória --input
    Quando eu executo "assinatura sign create --cert-id cert-001"
    Então o comando deve terminar com exit code 64
    E a saída deve conter "missing required flag: --input"

  Cenário: Falta flag obrigatória --cert-id
    Quando eu executo "assinatura sign create --input documento.txt"
    Então o comando deve terminar com exit code 64
    E a saída deve conter "missing required flag: --cert-id"

  Cenário: Comando inválido
    Quando eu executo "assinatura sign foo"
    Então o comando deve terminar com exit code 2
    E a saída deve sugerir "assinatura sign --help"

  Cenário: --help exibe ajuda
    Quando eu executo "assinatura sign create --help"
    Então o comando deve terminar com exit code 0
    E a saída deve conter "assinatura sign create"
    E a saída deve listar todas as flags disponíveis

  @critical
  Cenário: Assinador não está disponível e auto-start falha
    Dado que o Assinador não está rodando
    E o auto-start está desabilitado
    Quando eu executo "assinatura sign create --input documento.txt --cert-id cert-001"
    Então o comando deve terminar com exit code 70
    E a saída deve conter uma mensagem clara de erro

  Cenário: Saída em formato JSON
    Quando eu executo "assinatura sign create --input documento.txt --cert-id cert-001 --format json"
    Então o comando deve terminar com exit code 0
    E a saída deve ser JSON válido
    E o JSON deve conter o campo "signatureValue"

  Cenário: Salvar saída em arquivo
    Dado que o diretório "/tmp" tem permissão de escrita
    Quando eu executo "assinatura sign create --input documento.txt --cert-id cert-001 --output /tmp/resultado.txt"
    Então o comando deve terminar com exit code 0
    E o arquivo "/tmp/resultado.txt" deve existir
    E o arquivo deve conter uma assinatura simulada

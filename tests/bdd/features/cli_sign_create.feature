# language: pt
# User Story: US-CLI-01 — Invocar Assinador via CLI
# Componente: CLI Runner (Go)
# Comando real: assinatura sign --content <texto> [--local]

Funcionalidade: CLI Runner — Comando sign
  Como usuário do Sistema Runner
  Quero executar o comando "assinatura sign"
  Para criar uma assinatura digital simulada

  Contexto:
    Dado que a CLI Runner está instalada e disponível no PATH

  @critical
  Cenário: Criar assinatura com sucesso em modo local
    Quando eu executo "assinatura sign --content 'meu documento' --local"
    Então o comando deve terminar com exit code 0
    E a saída deve conter "Assinatura:"
    E a saída deve conter "Válido:"
    E a saída deve conter uma assinatura simulada começando com "RUNNER_SIM_SIG_"

  @critical
  Cenário: Criar assinatura com servidor ativo
    Dado que existe um servidor assinador.jar ativo na porta 8080
    Quando eu executo "assinatura sign --content 'meu documento'"
    Então o comando deve terminar com exit code 0
    E a saída deve conter uma assinatura simulada

  @critical
  Cenário: Falta flag obrigatória --content
    Quando eu executo "assinatura sign --local"
    Então o comando deve terminar com exit code 64
    E a saída deve conter "required flag(s) \"content\" not set"

  Cenário: Comando inválido
    Quando eu executo "assinatura foo"
    Então o comando deve terminar com exit code 2
    E a saída deve sugerir "assinatura --help"

  Cenário: --help exibe ajuda
    Quando eu executo "assinatura sign --help"
    Então o comando deve terminar com exit code 0
    E a saída deve listar todas as flags disponíveis
    E a saída deve mencionar "--content" e "--local"

  Cenário: --help do subcomando exibe ajuda
    Quando eu executo "assinatura help sign"
    Então o comando deve terminar com exit code 0
    E a saída deve mencionar "--content"

  @critical
  Cenário: Assinador não está disponível e modo local falha
    Dado que o Java não está disponível no PATH
    Quando eu executo "assinatura sign --content 'meu documento' --local"
    Então o comando deve terminar com exit code diferente de 0
    E a saída deve conter "Java não disponível"

  Cenário: Modo local — propagação de stdout do JAR
    Quando eu executo "assinatura sign --content 'teste propagação' --local"
    Então o comando deve terminar com exit code 0
    E o stdout deve conter o resultado da assinatura

  Cenário: Conteúdo com espaços é preservado
    Quando eu executo "assinatura sign --content 'documento com espaços e acentuação' --local"
    Então o comando deve terminar com exit code 0
    E a saída deve conter uma assinatura simulada
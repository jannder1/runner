# language: pt
# User Story: US-CLI-01 — Invocar Assinador via CLI
# Componente: CLI Runner (Go)
# Comando real: assinatura validate --content <texto> --signature <valor> [--local]

Funcionalidade: CLI Runner — Comando validate
  Como usuário do Sistema Runner
  Quero executar o comando "assinatura validate"
  Para validar uma assinatura digital simulada

  Contexto:
    Dado que a CLI Runner está instalada e disponível no PATH

  @critical
  Cenário: Validar assinatura válida em modo local
    Dado que uma assinatura foi criada para o conteúdo "documento original"
    Quando eu executo "assinatura validate --content 'documento original' --signature '<assinatura>' --local"
    Então o comando deve terminar com exit code 0
    E a saída deve conter "Válido: true"

  Cenário: Validar assinatura válida com servidor ativo
    Dado que existe um servidor assinador.jar ativo na porta 8080
    E que uma assinatura foi criada para o conteúdo "documento"
    Quando eu executo "assinatura validate --content 'documento' --signature '<assinatura>'"
    Então o comando deve terminar com exit code 0
    E a saída deve indicar que a assinatura é válida

  Cenário: Validar assinatura inválida (conteúdo divergente)
    Quando eu executo "assinatura validate --content 'conteúdo diferente' --signature 'RUNNER_SIM_SIG_invalida' --local"
    Então o comando deve terminar com exit code 0
    E a saída deve conter "Válido: false"
    E a saída deve conter uma mensagem indicando que a assinatura é inválida

  @critical
  Cenário: Flag obrigatória --content ausente
    Quando eu executo "assinatura validate --signature 'alguma' --local"
    Então o comando deve terminar com exit code 64
    E a saída deve conter "required flag(s) \"content\" not set"

  @critical
  Cenário: Flag obrigatória --signature ausente
    Quando eu executo "assinatura validate --content 'algo' --local"
    Então o comando deve terminar com exit code 64
    E a saída deve conter "required flag(s) \"signature\" not set"

  Cenário: --help exibe ajuda
    Quando eu executo "assinatura validate --help"
    Então o comando deve terminar com exit code 0
    E a saída deve listar todas as flags disponíveis
    E a saída deve mencionar "--content", "--signature" e "--local"

  Cenário: Conteúdo com espaços e acentuação é preservado
    Quando eu executo "assinatura validate --content 'olá mundo com acentuação' --signature 'qualquer' --local"
    Então o comando deve terminar com exit code 0
    E não deve ocorrer erro de parsing de argumentos
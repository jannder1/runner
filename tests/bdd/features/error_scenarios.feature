# language: pt
# Cenários transversais de erro (cobrem múltiplas US)
# Componente: CLI Runner + Assinador

Funcionalidade: Tratamento de Erros Transversais
  Como usuário do Sistema Runner
  Quero receber mensagens claras de erro
  Para entender o que deu errado e como corrigir

  @critical
  Cenário: Health check do Assinador
    Quando eu envio uma requisição GET para "/health"
    Então o status da resposta deve ser 200
    E o corpo deve conter "status": "UP"

  Cenário: Endpoint inexistente retorna 404
    Quando eu envio uma requisição GET para "/endpoint-que-nao-existe"
    Então o status da resposta deve ser 404

  Cenário: Método não permitido retorna 405
    Quando eu envio uma requisição PUT para "/sign"
    Então o status da resposta deve ser 405

  Cenário: Payload JSON malformado retorna 400
    Dado um payload JSON malformado "{ documentContent: 'falta aspas"
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400

  Cenário: Payload muito grande retorna 413
    Dado um payload JSON com documentContent de 10MB
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 413

  Cenário: Timeout de comunicação entre CLI e Assinador
    Dado que o Assinador não responde em 5 segundos
    Quando eu executo "assinatura sign create --input documento.txt --cert-id cert-001"
    Então o comando deve terminar com exit code 70
    E a mensagem de erro deve mencionar timeout

  Cenário: Porta 8080 já está em uso
    Dado que a porta 8080 está ocupada por outro processo
    Quando o Assinador tenta iniciar em modo servidor
    Então o processo deve terminar com exit code 48
    E a mensagem deve mencionar conflito de porta

  Cenário: Graceful shutdown via /shutdown
    Dado que o Assinador está rodando em modo servidor
    Quando eu envio uma requisição GET para "/shutdown"
    Então o Assinador deve encerrar graciosamente em até 5 segundos
    E novas requisições devem falhar com connection refused

  Cenário: Watchdog de inatividade desliga servidor ocioso
    Dado que o Assinador está rodando com timeout de 1 minuto
    Quando nenhuma requisição é feita por 2 minutos
    Então o Assinador deve encerrar automaticamente

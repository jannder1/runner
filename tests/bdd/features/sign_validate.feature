# language: pt
# User Story: US-AS-01 — Simular Assinatura Digital com Validação de Parâmetros
# Componente: Assinador (Java)
# Fonte: docs/04-backlog/US-AS-01.md

Funcionalidade: Validar Assinatura Digital Simulada
  Como integrador do Assinador
  Quero enviar uma assinatura para validação
  Para confirmar se ela é válida

  Contexto:
    Dado que o Assinador está rodando em modo servidor na porta 8080

  @critical
  Cenário: Validar assinatura válida
    Dado um payload JSON com signature "RUNNER_SIM_SIG_550e8400-e29b-41d4-a716-446655440000"
    Quando eu envio uma requisição POST para "/validate"
    Então o status da resposta deve ser 200
    E o campo "isValid" deve ser true

  @critical
  Cenário: Validar assinatura inválida (sem prefixo correto)
    Dado um payload JSON com signature "INVALID_SIGNATURE_123"
    Quando eu envio uma requisição POST para "/validate"
    Então o status da resposta deve ser 200
    E o campo "isValid" deve ser false

  Cenário: Validar assinatura com signature vazia
    Dado um payload JSON com signature ""
    Quando eu envio uma requisição POST para "/validate"
    Então o status da resposta deve ser 400
    E o campo "error" deve ser "User Error"

  Cenário: Validar sem campo signature
    Dado um payload JSON vazio
    Quando eu envio uma requisição POST para "/validate"
    Então o status da resposta deve ser 400

  Cenário: GET em /validate deve retornar 405
    Quando eu envio uma requisição GET para "/validate"
    Então o status da resposta deve ser 405

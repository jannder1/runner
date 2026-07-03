# language: pt
# User Story: US-AS-01 — Simular Assinatura Digital com Validação de Parâmetros
# Componente: Assinador (Java)
# Fonte: docs/04-backlog/US-AS-01.md

Funcionalidade: Criar Assinatura Digital Simulada
  Como integrador do Assinador
  Quero enviar uma requisição de criação de assinatura com parâmetros válidos
  Para receber uma assinatura simulada pré-construída

  Contexto:
    Dado que o Assinador está rodando em modo servidor na porta 8080

  @critical
  Cenário: Criar assinatura com parâmetros válidos
    Dado um payload JSON com:
      | campo            | valor                              |
      | documentContent  | "Conteúdo do documento a assinar"  |
      | practitionerId   | "pratic-001"                       |
      | systemOid        | "urn:oid:2.16.840.1.113883.4.1"   |
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 200
    E o corpo da resposta deve ser um JSON válido
    E o campo "signatureValue" deve começar com "RUNNER_SIM_SIG_"
    E o campo "isValid" deve ser true
    E o campo "timestamp" deve estar no formato ISO 8601
    E o campo "id" deve ser um UUID válido

  @critical
  Cenário: Rejeitar practitionerId com caracteres inválidos (uppercase)
    Dado um payload JSON com practitionerId "PRATIC-001"
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400
    E o campo "error" deve ser "User Error"
    E o campo "field" deve ser "practitionerId"
    E a mensagem deve mencionar "Invalid Practitioner ID"

  Cenário: Rejeitar practitionerId com tamanho inválido (>64 caracteres)
    Dado um payload JSON com practitionerId "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890"
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400
    E o campo "error" deve ser "User Error"
    E o campo "field" deve ser "practitionerId"

  @critical
  Cenário: Rejeitar systemOid sem prefixo urn:oid:
    Dado um payload JSON com systemOid "2.16.840.1.113883.4.1"
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400
    E o campo "error" deve ser "User Error"
    E a mensagem deve mencionar "Invalid System OID format"

  Cenário: Rejeitar systemOid com prefixo incorreto
    Dado um payload JSON com systemOid "oid:2.16.840.1.113883.4.1"
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400
    E o campo "error" deve ser "User Error"

  @critical
  Cenário: Rejeitar requisição sem documentContent
    Dado um payload JSON com documentContent null
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400
    E o campo "error" deve ser "User Error"
    E a mensagem deve mencionar "Document content is required"

  Cenário: Rejeitar requisição com documentContent vazio
    Dado um payload JSON com documentContent ""
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400
    E o campo "error" deve ser "User Error"

  Cenário: Rejeitar payload JSON vazio
    Dado um payload JSON vazio
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 400

  Cenário: Rejeitar requisição sem Content-Type JSON
    Dado um payload com Content-Type "text/plain"
    Quando eu envio uma requisição POST para "/sign"
    Então o status da resposta deve ser 415

  @critical
  Cenário: documentHash deve ser derivado do conteúdo
    Dado um payload JSON com documentContent "Conteúdo específico para teste de hash"
    Quando eu envio uma requisição POST para "/sign"
    Então o campo "documentHash" deve estar presente na resposta
    E o documentHash deve ser consistente em chamadas idênticas

  Cenário: Múltiplas assinaturas geram IDs únicos
    Quando eu envio 10 requisições POST para "/sign" com payload válido
    Então todos os 10 IDs retornados devem ser distintos

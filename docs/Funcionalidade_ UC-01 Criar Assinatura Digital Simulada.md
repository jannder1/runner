**Funcionalidade: UC-01 Criar Assinatura Digital Simulada**  
  **Como** usuário do sistema  
  **Quero** criar uma assinatura digital simulada  
  **Para** que documentos FHIR possam ser assinados conforme o guia SES GO

  **\# ─── RF-02 / Etapa 1.1** ────────────────────────────────────────────────────

  **Cenário: Política de assinatura ausente**  
    Dado que o usuário não forneceu a URI da política de assinatura  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código "POLICY.MISSING"  
    E a assinatura não é criada

  **Cenário: URI da política com formato inválido**  
    Dado que o usuário forneceu a URI "https://exemplo.com/politica"  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código "POLICY.URI-INVALID"  
    E a assinatura não é criada

  **Cenário: Versão da política não suportada**  
    Dado que o usuário forneceu a URI   
    *"https://fhir.saude.go.gov.br/r4/seguranca/ImplementationGuide/br.go.ses.seguranca|9.9.9"*  
    **Quando** o comando assinar é executado  
    **Então** o sistema retorna OperationOutcome com código "POLICY.VERSION-UNSUPPORTED"  
    **E** o diagnóstico informa a versão solicitada e as versões suportadas  
    **E** a assinatura não é criada

  **\# ─── RF-02 / Etapa 1.2** ────────────────────────────────────────────────────

  **Cenário: Timestamp de referência com formato inválido**  
    Dado que o usuário forneceu o timestamp "abc123"  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "CONFIG.INVALID-TIMESTAMP-FORMAT"  
    E a assinatura não é criada

  **Cenário: Timestamp de referência fora do intervalo permitido**  
    Dado que o usuário forneceu o timestamp "1000000000"  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "CONFIG.TIMESTAMP-OUT-OF-RANGE"  
    E a assinatura não é criada

  **\# ─── RF-02 / Etapa 1.3** ────────────────────────────────────────────────────

  **Cenário: Estratégia de timestamp inválida**  
    Dado que o usuário forneceu a estratégia "blockchain"  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "CONFIG.INVALID-STRATEGY"  
    E a assinatura não é criada

  **Cenário: Estratégia tsa sem configuração de URL da TSA**  
    Dado que o usuário forneceu a estratégia "tsa"  
    E que o usuário não forneceu a URL da TSA nas configurações operacionais  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "CONFIG.TSA-CONFIG-MISSING"  
    E a assinatura não é criada

  \# ─── RF-02 / Etapa 1.4 ────────────────────────────────────────────────────

  **Cenário: Bundle FHIR malformado**  
    Dado que o usuário forneceu um Bundle que não está em conformidade com FHIR R4  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código "FORMAT.BUNDLE-MALFORMED"  
    E a assinatura não é criada

  **Cenário: Bundle FHIR sem entradas**  
    Dado que o usuário forneceu um Bundle sem nenhuma entrada em Bundle.entry  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código "FORMAT.BUNDLE-EMPTY"  
    E a assinatura não é criada

  **Cenário: Bundle com fullUrl duplicado**  
    Dado que o usuário forneceu um Bundle com duas entradas   
      com o mesmo fullUrl "urn:uuid:abc123"  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código "FORMAT.BUNDLE-MALFORMED"  
    E a assinatura não é criada

  **Cenário: Bundle excede número máximo de entradas**  
    Dado que o usuário forneceu um Bundle com 1001 entradas  
    E que a configuração maxEntriesBundle é 1000  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "SECURITY.BUNDLE-SIZE-LIMIT-EXCEEDED"  
    E a assinatura não é criada

  \# ─── RF-02 / Etapa 1.5 ────────────────────────────────────────────────────

  **Cenário: Provenance sem targets**  
    Dado que o usuário forneceu um Provenance sem nenhuma referência   
      em Provenance.target  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "FORMAT.PROVENANCE-INVALID"  
    E a assinatura não é criada

  **Cenário: Provenance com target duplicado**  
    Dado que o usuário forneceu um Provenance com duas referências   
      idênticas em Provenance.target  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "FORMAT.PROVENANCE-TARGET-DUPLICATE"  
    E a assinatura não é criada

  **Cenário: Target do Provenance com formato de referência inválido**  
    Dado que o usuário forneceu um Provenance com target "http://exemplo.com/recurso"  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "FORMAT.PROVENANCE-TARGET-INVALID"  
    E a assinatura não é criada

  \# ─── RF-02 / Etapa 1.6 ────────────────────────────────────────────────────

  **Cenário: Referência do Provenance não encontrada no Bundle**  
    Dado que o Provenance referencia "urn:uuid:nao-existe"  
    E que nenhuma entrada do Bundle possui fullUrl "urn:uuid:nao-existe"  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "FORMAT.TARGET-REFERENCE-MISSING"  
    E a assinatura não é criada

  \# ─── RF-02 / Etapa 1.9 ────────────────────────────────────────────────────

  **Cenário: Referência interna inválida dentro de recurso assinado**  
    Dado que uma instância referenciada contém um elemento Reference com   
      reference e identifier simultaneamente preenchidos  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código "FORMAT.REFERENCE-INVALID"  
    E o diagnóstico indica a instância e o elemento problemático  
    E a assinatura não é criada

  \# ─── RF-02 / Etapa 1.10 ───────────────────────────────────────────────────

  **Cenário: Timestamp de referência fora da janela de tolerância do servidor**  
    Dado que o timestamp de referência difere em 400 segundos do relógio do servidor  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "TIMESTAMP.OUT-OF-TOLERANCE-WINDOW"  
    E a assinatura não é criada

  \# ─── RF-06 / Estratégia iat ───────────────────────────────────────────────

  **Cenário: Timestamp iat fora do período de validade do certificado**  
    Dado que o usuário forneceu a estratégia "iat"  
    E que o timestamp de referência é anterior ao notBefore do certificado  
    Quando o comando assinar é executado  
    Então o sistema retorna OperationOutcome com código   
      "TEMPORAL.IAT-OUT-OF-CERT-PERIOD"  
    E a assinatura não é criada

  \# ─── RF-04 / Fluxo feliz iat ──────────────────────────────────────────────

  **Cenário: Criação de assinatura simulada com sucesso usando estratégia iat**  
    Dado que o usuário forneceu um Bundle FHIR R4 válido com uma entrada  
    E que o usuário forneceu um Provenance FHIR R4 válido   
      referenciando a entrada do Bundle  
    E que o usuário forneceu material criptográfico simulado válido  
    E que o usuário forneceu uma cadeia de certificados simulada válida  
    E que o usuário forneceu o timestamp de referência "1751328100"  
    E que o usuário forneceu a estratégia "iat"  
    E que o usuário forneceu a URI de política válida e suportada  
    E que o usuário forneceu configurações operacionais válidas  
    Quando o comando assinar é executado  
    Então o sistema retorna uma instância Signature FHIR R4  
    E Signature.sigFormat é "application/jose"  
    E Signature.targetFormat é "application/octet-stream"  
    E Signature.data contém uma string base64 válida  
    E o JWS decodificado possui protected header com campos alg, x5c, iat e sigPId  
    E o JWS decodificado possui unprotected header com campo rRefs  
    E o campo rRefs contém ocspRefs ou crlRefs com digestAlg SHA-512

  \# ─── RF-04 / Fluxo feliz tsa ──────────────────────────────────────────────

  **Cenário: Criação de assinatura simulada com sucesso usando estratégia tsa**  
    Dado que o usuário forneceu entradas válidas conforme cenário anterior  
    E que o usuário forneceu a estratégia "tsa"  
    E que o usuário forneceu URL de TSA válida nas configurações operacionais  
    Quando o comando assinar é executado  
    Então o sistema retorna uma instância Signature FHIR R4  
    E o JWS decodificado possui protected header sem campo iat  
    E o JWS decodificado possui unprotected header com campos sigTst e rRefs

  \# ─── RF-11 ────────────────────────────────────────────────────────────────

  **Cenário: Elementos não assinados são removidos antes da assinatura**  
    Dado que o Bundle fornecido contém instâncias com id, meta.versionId   
      e meta.lastUpdated  
    E que as instâncias também contêm meta.profile e meta.security  
    Quando o comando assinar é executado com entradas válidas  
    Então o hash calculado não considera id, meta.versionId,   
      meta.lastUpdated, meta.source e meta.tag  
    E o hash calculado considera meta.profile e meta.security

  \# ─── RF-10 ────────────────────────────────────────────────────────────────

  **Cenário: Canonicalização RFC 8785 é aplicada antes do hash**  
    Dado que duas instâncias JSON semanticamente idênticas   
      mas com ordem de propriedades diferente são fornecidas  
    Quando o comando assinar é executado para cada uma separadamente  
    Então o payload do JWS resultante é idêntico nos dois casos

**Funcionalidade: UC-02 Validar Assinatura Digital Simulada**  
  Como usuário do sistema  
  Quero validar uma assinatura digital simulada  
  Para que eu possa verificar a autenticidade e integridade de documentos FHIR

  \# ─── RF-03 / Etapa 0 ─────────────────────────────────────────────────────

  **Cenário: Configurações operacionais com trust store vazio**  
    Dado que as configurações operacionais possuem trust store sem certificados raiz  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "CONFIG.TRUST-STORE-EMPTY"  
    E a validação é interrompida antes de qualquer processamento criptográfico

  **Cenário: Timeout fora do intervalo permitido nas configurações**  
    Dado que as configurações operacionais possuem ocspTimeout com valor "200"  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código   
      "CONFIG.TIMEOUT-OUT-OF-RANGE"  
    E a validação é interrompida

  **Cenário: Política de revogação com valor inválido**  
    Dado que as configurações operacionais possuem revocationPolicy "total"  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "CONFIG.INVALID-PARAMETER"  
    E a validação é interrompida

  \# ─── RF-05 / Etapa 1 ─────────────────────────────────────────────────────

  **Cenário: Signature.data com base64 inválido**  
    Dado que o usuário forneceu um Signature.data que não é base64 válido  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "FORMAT.BASE64-INVALID"  
    E a validação é interrompida

  **Cenário: JWS JSON com estrutura malformada**  
    Dado que o Signature.data decodificado não contém a propriedade "signatures"  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "FORMAT.JWS-MALFORMED"  
    E a validação é interrompida

  \# ─── RF-05 / Etapa 2 ─────────────────────────────────────────────────────

  **Cenário: Algoritmo do protected header não suportado**  
    Dado que o protected header do JWS contém alg "RS512"  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código   
      "VALIDATION.UNSUPPORTED-ALGORITHM"  
    E a validação é interrompida

  **Cenário: Política do protected header não suportada**  
    Dado que o protected header contém sigPId com versão "9.9.9"  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código   
      "POLICY.VERSION-UNSUPPORTED"  
    E a validação é interrompida

  **Cenário: Evidências LTV com estrutura inválida no unprotected header**  
    Dado que o unprotected header contém rRefs com digestAlg diferente de   
      "http://www.w3.org/2001/04/xmlenc\#sha512"  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código   
      "VALIDATION.LTV-EVIDENCE-INVALID"  
    E a validação é interrompida

  **Cenário: Estratégia de timestamp ambígua**  
    Dado que o protected header contém iat  
    E que o unprotected header contém sigTst simultaneamente  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código   
      "VALIDATION.TIMESTAMP-STRATEGY-INVALID"  
    E a validação é interrompida

  \# ─── RF-05 / Etapa 3 ─────────────────────────────────────────────────────

  **Cenário: Cadeia de certificados com menos de dois certificados**  
    Dado que o campo x5c do protected header contém apenas um certificado  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "CERT.CHAIN-INCOMPLETE"  
    E a validação é interrompida

  **Cenário: Certificado raiz não pertence à ICP-Brasil**  
    Dado que o último certificado da cadeia não corresponde a nenhuma   
      AC Raiz ICP-Brasil no trust store  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "CERT.NOT-ICP-BRASIL"  
    E a validação é interrompida

  **Cenário: Certificado do signatário expirado em relação ao timestamp de referência**  
    Dado que o notAfter do certificado do signatário é anterior   
      ao timestamp de referência fornecido  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "CERT.EXPIRED"  
    E a validação é interrompida

  **Cenário: Certificado com chave RSA de tamanho insuficiente**  
    Dado que o certificado do signatário possui chave RSA de 1024 bits  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "CERT.WEAK-KEY"  
    E a validação é interrompida

  \# ─── RF-05 / Etapa 4 ─────────────────────────────────────────────────────

  **Cenário: Assinatura criptográfica inválida**  
    Dado que a assinatura JWS foi gerada com dados diferentes do payload atual  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código   
      "VALIDATION.SIGNATURE-VERIFICATION-FAILED"  
    E a validação é interrompida

  \# ─── RF-05 / Etapa 6 ─────────────────────────────────────────────────────

  **Cenário: Timestamp iat fora do período de validade do certificado na validação**  
    Dado que o protected header contém iat anterior ao notBefore do certificado  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código   
      "TEMPORAL.IAT-OUT-OF-CERT-PERIOD"  
    E a validação é interrompida

  **Cenário: Token TSA inválido na validação**  
    Dado que o unprotected header contém sigTst com token TSA malformado  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "TSA.VALIDATION-FAILED"  
    E a validação é interrompida

  \# ─── RF-05 / Fluxo feliz ──────────────────────────────────────────────────

  **Cenário: Validação de assinatura simulada com sucesso**  
    Dado que o usuário forneceu um Signature.data válido   
      produzido pelo caso de uso de criação  
    E que as configurações operacionais são válidas  
    E que o timestamp de referência está dentro do intervalo aceito  
    E que a URI da política é suportada  
    Quando o comando validar é executado  
    Então o sistema retorna OperationOutcome com código "VALIDATION.SUCCESS"  
    E issue.severity é "information"  
    E issue.diagnostics contém o algoritmo e a estratégia de timestamp utilizados

  \# ─── RF-05 / Verificação opcional de integridade ─────────────────────────

  **Cenário: Verificação opcional de integridade com conteúdo íntegro**  
    Dado que o usuário forneceu Signature.data válida  
    E que o usuário forneceu o Bundle original  
    E que o usuário forneceu o Provenance original  
    E que o conteúdo não foi alterado desde a assinatura  
    Quando o comando validar é executado com verificação de integridade habilitada  
    Então o sistema confirma que o payload do JWS corresponde   
      ao hash recalculado do conteúdo  
    E retorna OperationOutcome com código "VALIDATION.SUCCESS"

  **Cenário: Verificação opcional de integridade detecta conteúdo alterado**  
    Dado que o usuário forneceu Signature.data válida  
    E que o usuário forneceu um Bundle com conteúdo diferente do original assinado  
    Quando o comando validar é executado com verificação de integridade habilitada  
    Então o payload do JWS não corresponde ao hash recalculado  
    E o sistema retorna OperationOutcome indicando inconsistência de integridade

**Funcionalidade: UC-03 Invocar assinador via CLI em modo local**  
  Como usuário da linha de comando  
  Quero executar operações de assinatura e validação via CLI  
  Para que eu não precise conhecer detalhes da API HTTP

  \# ─── RF-01 / RF-13 ───────────────────────────────────────────────────────

  **Cenário: Comando assinar em modo local com entradas válidas**  
    Dado que o assinador.jar está disponível no sistema  
    E que o usuário executou "assinatura assinar \--bundle bundle.json   
      \--provenance prov.json \--cert cadeia.json \--config config.json"  
    Quando a CLI processa o comando em modo local  
    Então a CLI invoca o assinador.jar como subprocesso  
    E envia os parâmetros via stdin em formato JSON  
    E exibe o resultado recebido do stdout do subprocesso  
    E encerra com código de saída 0

  **Cenário: Comando assinar com parâmetro obrigatório ausente**  
    Dado que o usuário executou "assinatura assinar \--bundle bundle.json"  
    Quando a CLI processa o comando  
    Então a CLI exibe mensagem de uso indicando o parâmetro ausente  
    E encerra com código de saída diferente de 0  
    E não invoca o assinador.jar

  **Cenário: Comando validar em modo local com entradas válidas**  
    Dado que o assinador.jar está disponível no sistema  
    E que o usuário executou "assinatura validar \--assinatura sig.json   
      \--config config.json"  
    Quando a CLI processa o comando em modo local  
    Então a CLI invoca o assinador.jar como subprocesso  
    E exibe o OperationOutcome retornado  
    E encerra com código de saída 0 quando VALIDATION.SUCCESS

  \# ─── RF-12 / RF-01 ───────────────────────────────────────────────────────

  **Cenário: Comando assinar em modo servidor**  
    Dado que o assinador.jar está em execução na porta 8080  
    E que o usuário executou "assinatura assinar \--bundle bundle.json   
      \--provenance prov.json \--cert cadeia.json \--config config.json   
      \--servidor http://localhost:8080"  
    Quando a CLI processa o comando em modo servidor  
    Então a CLI envia uma requisição HTTP POST para o endpoint de assinatura  
    E exibe o resultado da resposta HTTP  
    E encerra com código de saída 0 em caso de sucesso  

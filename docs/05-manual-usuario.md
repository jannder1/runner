# Manual de Usuário — Sistema Runner

> **Versão:** 1.0
> **Público-alvo:** Usuários finais da CLI Runner
> **Disciplina:** Implementação e Integração (2026-01)

---

## 1. O que é o Sistema Runner?

O **Sistema Runner** é uma ferramenta de linha de comandos (CLI) que permite executar aplicações Java relacionadas a **assinatura digital simulada** sem que você precise entender detalhes técnicos de configuração Java, Maven, ou instalação de JDK.

> ⚠️ **Importante:** o sistema **simula** operações de assinatura digital. Não use para documentos que requerem assinatura criptográfica real.

### 1.1. Componentes do sistema

| Componente | O que faz | Quem executa |
|---|---|---|
| **CLI Runner** (`assinatura`) | Recebe comandos do usuário, orquestra operações | Você, no terminal |
| **Assinador** (`assinador.jar`) | Valida parâmetros FHIR e simula assinaturas | A CLI Runner, automaticamente |
| **Simulador HubSaúde** (`simulador.jar`) | Sistema externo que a CLI pode gerenciar | A CLI Runner, automaticamente |

### 1.2. Para quem é este manual

Este manual é para o **usuário final** que vai usar a CLI Runner no terminal. Você não precisa saber programar para usar.

---

## 2. Instalação

### 2.1. Requisitos

- **Sistema Operacional:** Windows 10/11, Linux (Ubuntu 20.04+), macOS 11+
- **Arquitetura:** amd64 (Intel/AMD 64 bits)
- **Espaço em disco:** ~150 MB (incluindo JDK provisionado automaticamente)
- **Conexão à internet:** apenas na primeira execução (para baixar JDK)

### 2.2. Download

Acesse a página de [Releases do projeto](https://github.com/jannder1/runner/releases) e baixe o binário correspondente ao seu sistema:

| Plataforma | Arquivo |
|------------|---------|
| Windows (amd64) | `assinatura-windows-amd64.exe` |
| Linux (amd64) | `assinatura-linux-amd64` |
| macOS (amd64) | `assinatura-darwin-amd64` |

### 2.3. Verificação de integridade

Após o download, **verifique o checksum SHA256** para garantir que o arquivo não foi corrompido:

**Linux / macOS:**
```bash
sha256sum -c SHA256SUMS
```

Saída esperada:
```
assinatura-linux-amd64: OK
```

**Windows (PowerShell):**
```powershell
Get-FileHash assinatura-windows-amd64.exe -Algorithm SHA256
```

Compare o hash gerado com o valor listado em `SHA256SUMS`.

### 2.4. Instalação do binário

#### Linux / macOS

```bash
# Torne o binário executável
chmod +x assinatura-linux-amd64

# Mova para um diretório no PATH (opcional, mas recomendado)
sudo mv assinatura-linux-amd64 /usr/local/bin/assinatura

# Verifique a instalação
assinatura version
```

#### Windows

```powershell
# Renomeie para 'assinatura.exe'
Rename-Item assinatura-windows-amd64.exe assinatura.exe

# Adicione ao PATH (via Configurações do Sistema):
# 1. Abra "Editar variáveis de ambiente do sistema"
# 2. Em "Path", adicione o diretório onde está assinatura.exe
# 3. Reinicie o terminal

# Verifique a instalação
assinatura version
```

### 2.5. JDK

**Não é necessário instalar Java manualmente.** Na primeira execução, o Sistema Runner baixa e configura automaticamente o JDK necessário (Eclipse Temurin 21 LTS).

Se você já tem Java 21+ instalado e configurado via `JAVA_HOME`, o Runner detecta e usa o existente.

---

## 3. Primeiros Passos

### 3.1. Verificar a instalação

```bash
assinatura version
```

Saída esperada:
```
assinatura version 0.1.0
```

### 3.2. Exibir ajuda geral

```bash
assinatura --help
```

### 3.3. Ajuda de um comando específico

```bash
assinatura sign --help
assinatura sign create --help
```

---

## 4. Comandos

### 4.1. `version` — Exibir versão

Exibe a versão da CLI Runner.

```bash
assinatura version
```

**Saída:**
```
assinatura version 0.1.0
```

---

### 4.2. `sign create` — Criar assinatura simulada

Cria uma assinatura digital simulada para um documento.

**Sintaxe:**
```bash
assinatura sign create --input <arquivo> --cert-id <id> [flags opcionais]
```

**Flags obrigatórias:**

| Flag | Descrição | Exemplo |
|------|-----------|---------|
| `--input` | Caminho do documento a assinar | `--input documento.txt` |
| `--cert-id` | ID do certificado (referência) | `--cert-id cert-001` |

**Flags opcionais:**

| Flag | Descrição | Padrão |
|------|-----------|--------|
| `--output` | Arquivo de saída (em vez de stdout) | stdout |
| `--format` | Formato de saída: `text` ou `json` | `text` |

**Exemplo:**
```bash
assinatura sign create --input contrato.pdf --cert-id cert-empresa-01
```

**Saída esperada (text):**
```
Assinatura criada com sucesso!
ID: 550e8400-e29b-41d4-a716-446655440000
Hash: a1b2c3d4
Signature: RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7
Timestamp: 2026-06-23T18:30:00Z
```

**Exemplo com JSON:**
```bash
assinatura sign create --input contrato.pdf --cert-id cert-001 --format json
```

**Saída:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "documentHash": "a1b2c3d4",
  "signatureValue": "RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "timestamp": "2026-06-23T18:30:00Z",
  "certificateSubject": "CN=Runner Simulator, O=UFG, C=BR",
  "isValid": true
}
```

---

### 4.3. `sign validate` — Validar assinatura simulada

Valida se uma assinatura simulada é válida.

**Sintaxe:**
```bash
assinatura sign validate --signature <valor> [flags]
```

**Flags obrigatórias:**

| Flag | Descrição |
|------|-----------|
| `--signature` | Valor da assinatura a validar |

**Exemplo:**
```bash
assinatura sign validate --signature "RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7"
```

**Saída (assinatura válida):**
```
Assinatura válida ✓
ID: 550e8400-e29b-41d4-a716-446655440000
```

**Saída (assinatura inválida):**
```
Assinatura inválida ✗
```
Exit code: 65

---

### 4.4. `simulator start` — Iniciar o Simulador

Inicia o Simulador HubSaúde em background.

```bash
assinatura simulator start
```

**Saída:**
```
Simulador iniciado (PID: 12345)
URL: http://localhost:8081
```

**Opções:**

| Flag | Descrição | Padrão |
|------|-----------|--------|
| `--simulator-path` | Caminho do `simulador.jar` | `./simulador.jar` |
| `--port` | Porta do simulador | `8081` |

---

### 4.5. `simulator stop` — Parar o Simulador

Para o Simulador HubSaúde em execução.

```bash
assinatura simulator stop
```

**Saída:**
```
Simulador parado (PID 12345 encerrado)
```

---

### 4.6. `simulator status` — Status do Simulador

Exibe o estado atual do Simulador.

```bash
assinatura simulator status
```

**Saída (rodando):**
```
Status: running
PID:    12345
Uptime: 2h 15m
URL:    http://localhost:8081
```

**Saída (parado):**
```
Status: stopped
```

---

## 5. Exemplos Práticos

### 5.1. Exemplo 1: Assinar um documento de texto

```bash
# Criar um documento de teste
echo "Conteúdo do contrato entre as partes..." > contrato.txt

# Assinar o documento
assinatura sign create --input contrato.txt --cert-id cert-empresa-01

# Anotar o signatureValue retornado
# (Exemplo: RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7)

# Validar a assinatura mais tarde
assinatura sign validate --signature "RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7"
```

### 5.2. Exemplo 2: Pipeline de processamento

```bash
#!/bin/bash
# assinar-lote.sh — Assina múltiplos documentos

for doc in documentos/*.pdf; do
  echo "Assinando $doc..."
  assinatura sign create --input "$doc" --cert-id "cert-batch-$(date +%s)"
done
```

### 5.3. Exemplo 3: Integração com scripts JSON

```bash
# Capturar resultado em JSON para processamento
RESULT=$(assinatura sign create --input doc.txt --cert-id cert-001 --format json)
echo "$RESULT" | jq '.signatureValue'

# Validar programaticamente
if assinatura sign validate --signature "$(echo "$RESULT" | jq -r '.signatureValue')" > /dev/null 2>&1; then
  echo "Documento assinado e validado com sucesso"
else
  echo "Falha na validação"
  exit 1
fi
```

---

## 6. Tratamento de Erros

A CLI Runner retorna **exit codes** padronizados para indicar sucesso ou falha:

| Código | Significado | Quando ocorre |
|--------|-------------|---------------|
| `0` | Sucesso | Operação concluída com êxito |
| `2` | Uso incorreto | Comando ou flag desconhecida |
| `64` | Argumentos inválidos | Flag obrigatória ausente ou formato incorreto |
| `65` | Dados inválidos | Assinatura inválida, parâmetros FHIR rejeitados |
| `70` | Erro interno | Falha de comunicação com Assinador, exceção não tratada |
| `78` | Erro de configuração | Plataforma não suportada, arquivo de configuração inválido |
| `130` | Interrompido pelo usuário | Ctrl+C durante operação |

### 6.1. Mensagens comuns

**"missing required flag: --input"**
- Solução: inclua a flag `--input <arquivo>` no comando

**"Assinador não está respondendo"**
- Solução: a CLI tenta iniciar o Assinador automaticamente. Se falhar, execute `assinatura --verbose sign create ...` para mais detalhes

**"Plataforma windows-arm64 não suportada"**
- Solução: use arquitetura amd64 (Intel/AMD 64 bits)

**"Documento não encontrado: contrato.txt"**
- Solução: verifique o caminho do arquivo. Use caminho absoluto se necessário

---

## 7. Perguntas Frequentes (FAQ)

### 7.1. Preciso ter Java instalado?

**Não necessariamente.** Na primeira execução, o Sistema Runner baixa o JDK automaticamente. Se você já tem JDK 21+ via `JAVA_HOME`, ele usa esse.

### 7.2. A assinatura gerada é real (criptograficamente válida)?

**Não.** Este sistema **simula** operações de assinatura digital. Use apenas para testes, desenvolvimento, ou estudos. Não use para documentos legais.

### 7.3. Posso usar em ARM (Apple Silicon, Raspberry Pi)?

Atualmente não. Apenas arquitetura amd64 é suportada oficialmente.

### 7.4. Onde fica armazenado o JDK baixado?

- **Linux/macOS:** `~/.runner/jdk/`
- **Windows:** `%USERPROFILE%\.runner\jdk\`

Para reinstalar, basta deletar esse diretório.

### 7.5. Como desinstalar?

1. Delete o binário (`assinatura` ou `assinatura.exe`)
2. Delete o diretório `~/.runner/` (remove JDK provisionado e configurações)

### 7.6. Como atualizar para uma nova versão?

Baixe a versão mais recente da [página de Releases](https://github.com/jannder1/runner/releases) e substitua o binário antigo. Configurações e JDK em cache são preservados.

---

## 8. Onde Buscar Ajuda

- **Issues/Bugs:** [github.com/jannder1/runner/issues](https://github.com/jannder1/runner/issues)
- **Discussões:** [github.com/jannder1/runner/discussions](https://github.com/jannder1/runner/discussions)
- **Email:** [contato da equipe]
- **Documentação técnica:** [`docs/02-design.md`](./02-design.md)

---

## 9. Glossário Rápido

| Termo | Significado |
|-------|-------------|
| **CLI** | Interface de linha de comandos (você digita comandos no terminal) |
| **FHIR** | Padrão de dados de saúde (define formato dos parâmetros) |
| **JDK** | Java Development Kit (necessário para rodar o Assinador) |
| **Assinatura digital** | Operação criptográfica que prova autenticidade de documento |
| **Simulação** | Reprodução do comportamento sem operação real (não usa criptografia) |
| **Exit code** | Número retornado pelo comando ao sistema operacional indicando sucesso/falha |

---

**Versão do manual:** 1.0 (Junho/2026)
**Compatível com:** Sistema Runner v0.1.0+
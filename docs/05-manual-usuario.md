[manual-usuario-v2.md](https://github.com/user-attachments/files/29616594/manual-usuario-v2.md)
# Manual de Usuário — Sistema Runner

> **Versão:** 2.0 (consertada para refletir o código real)
> **Público-alvo:** Usuários finais da CLI Runner
> **Disciplina:** Implementação e Integração (2026-01)
> **Compatível com:** Sistema Runner v0.2.0+

> ⚠️ **Atenção — este manual foi reescrito.** A versão 1.0 (junho/2026)
> documentava comandos que não existem no código  (`sign create`,
> `sign validate`, `simulator start/stop/status`). Esta versão descreve
> os comandos que de fato existem: `version`, `sign`, `validate`,
> `start`, `stop`, `run`.

---

## 1. O que é o Sistema Runner?

O **Sistema Runner** é uma ferramenta de linha de comandos (CLI) que permite executar aplicações Java relacionadas a **assinatura digital simulada** sem que você precise entender detalhes técnicos de configuração Java, Maven, ou instalação de JDK.

> ⚠️ **Importante:** o sistema **simula** operações de assinatura digital. Não use para documentos que requerem assinatura criptográfica real.

### 1.1. Componentes do sistema

| Componente | O que faz | Quem executa |
|---|---|---|
| **CLI Runner** (`assinatura`) | Recebe comandos do usuário, orquestra operações | Você, no terminal |
| **Assinador** (`assinador.jar`) | Valida parâmetros FHIR e simula assinaturas | A CLI Runner, automaticamente |

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
assinatura v0.2.0 (a1b2c3d)
```

### 3.2. Exibir ajuda geral

```bash
assinatura --help
```

### 3.3. Ajuda de um comando específico

```bash
assinatura sign --help
assinatura start --help
```

---

## 4. Modos de operação

A CLI opera em dois modos, escolhidos automaticamente:

| Modo | Quando usado | Como forçar |
|---|---|---|
| **Servidor (padrão)** | Existe servidor ativo respondendo ao `/health` | (automático) |
| **Local (subprocess)** | Nenhum servidor ativo, OU flag `--local` | `--local` em `sign` ou `validate` |

O critério para "servidor ativo" é: existe arquivo `~/.hubsaude/assinador.pid`
válido E a porta registrada responde `200 OK` em `GET /health` em até 2
segundos. Ver ADR-003 em `docs/adr/`.

Em resumo:
- Se você só vai rodar **um comando pontual**, use `sign --local`.
- Se você vai rodar **vários comandos em sequência**, faça `start` uma vez
  e depois `sign` / `validate` à vontade (mais rápido).

---

## 5. Comandos

### 5.1. `version` — Exibir versão

Imprime a versão do CLI no formato `assinatura <tag> (<sha-curto>)` ou
`assinatura dev` em builds locais.

```bash
$ assinatura version
assinatura v0.2.0 (a1b2c3d)
```

A versão é injetada em build-time via `-ldflags`. Não há flag.

---

### 5.2. `sign` — Criar assinatura simulada

Cria uma assinatura digital simulada do conteúdo fornecido.

**Sintaxe:**
```bash
assinatura sign --content <texto> [--local]
```

**Flags:**

| Flag | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `--content` | string | sim | Conteúdo a ser assinado |
| `--local` | bool | não | Força modo local (subprocess) mesmo com servidor ativo |

**Comportamento:**

1. Se houver servidor ativo e `--local` não foi passado, faz `POST /sign`
   com `{"content": "<texto>"}` e imprime `Assinatura`, `Válido`, `Mensagem`.
2. Caso contrário, executa `java -jar assinador.jar sign --content <texto>`
   e propaga stdout/stderr.

**Exemplos:**

```bash
# Modo padrão (servidor, se disponível)
assinatura sign --content "documento único"

# Forçar modo local (subprocess)
assinatura sign --content "documento único" --local
```

**Saída típica:**

```
Assinatura: RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7
Válido: true
Mensagem: Assinatura simulada criada com sucesso
```

**Possíveis erros:**

- `"obrigatório: --content"` — flag ausente.
- `"Java não disponível"` — JDK não encontrado e provisionamento falhou.
- `"assinador.jar não encontrado"` — JAR ausente e download falhou.
- `"erro na requisição HTTP"` — servidor não respondeu ou retornou JSON inválido.

---

### 5.3. `validate` — Validar assinatura simulada

Valida uma assinatura previamente criada.

**Sintaxe:**
```bash
assinatura validate --content <texto> --signature <valor> [--local]
```

**Flags:**

| Flag | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `--content` | string | sim | Conteúdo original |
| `--signature` | string | sim | Assinatura a validar |
| `--local` | bool | não | Força modo local (subprocess) mesmo com servidor ativo |

**Exemplos:**

```bash
assinatura validate --content "documento" --signature "RUNNER_SIM_SIG_..."
assinatura validate --content "documento" --signature "RUNNER..." --local
```

**Saída (válida):**

```
Assinatura: RUNNER_SIM_SIG_...
Válido: true
Mensagem: Assinatura válida
```

**Saída (inválida):**

```
Assinatura: RUNNER_SIM_SIG_...
Válido: false
Mensagem: Assinatura inválida ou conteúdo divergente
```

Exit code: `65` (dados inválidos).

---

### 5.4. `start` — Subir o assinador.jar em background

Sobe o `assinador.jar` como servidor HTTP em background, permitindo que
comandos `sign` e `validate` subsequentes sejam atendidos com baixa latência.

**Sintaxe:**
```bash
assinatura start [--port <N>] [--timeout <minutos>]
```

**Flags:**

| Flag | Tipo | Default | Descrição |
|---|---|---|---|
| `--port` | int | 8080 | Porta HTTP do servidor |
| `--timeout` | int | 0 | Inatividade máxima em minutos antes de auto-shutdown. `0` desativa. |

**Comportamento (idempotente):**

1. Lê `~/.hubsaude/assinador.pid` (se existir).
2. Se o PID registrado está vivo E a porta responde `/health` com 200,
   imprime `"Servidor já em execução..."` e sai com sucesso.
3. Caso contrário, executa `java -jar assinador.jar serve --port <N>` em
   background, aguardando até 30s pelo `GET /health` retornar 200.
4. Grava o PID e a porta em `~/.hubsaude/assinador.pid`.
5. Injeta `HUBSAUDE_TIMEOUT_MINUTES=<N>` no ambiente se `--timeout > 0`.

> 💡 **Dica:** rodar `start` duas vezes seguidas é seguro — a segunda
> chamada detecta a instância viva e não sobe processo duplicado.

**Exemplos:**

```bash
# Subir na porta padrão 8080, sem auto-shutdown
assinatura start

# Subir na porta 9090
assinatura start --port 9090

# Subir com auto-shutdown de 30min
assinatura start --port 9090 --timeout 30
```

**Possíveis erros:**

- `"porta inválida"` — argumento de `--port` não é número inteiro.
- `"Java não disponível"` — JDK ausente.
- `"servidor não respondeu após 30 segundos"` — JAR subiu mas health check
  falhou (porta tomada por outro processo? código quebrou?).

---

### 5.5. `stop` — Encerrar o assinador.jar

Encerra o servidor `assinador.jar` que está rodando em background.

**Sintaxe:**
```bash
assinatura stop [--port <N>]
```

**Flags:**

| Flag | Tipo | Default | Descrição |
|---|---|---|---|
| `--port` | int | 0 (usa pid file) | Porta esperada. Se passada e diferente da registrada, aborta. |

**Comportamento:**

1. Lê `~/.hubsaude/assinador.pid`.
2. Se `--port` foi passada e difere da registrada, aborta com erro.
3. Envia SIGTERM ao PID registrado (com fallback para SIGKILL após 5s).
4. Aguarda shutdown gracioso (Spring `@PreDestroy`).
5. Remove o pid file.

**Exemplos:**

```bash
assinatura stop
assinatura stop --port 9090
```

**Possíveis erros:**

- `"nenhum servidor registrado"` — pid file não existe.
- `"nenhum servidor registrado na porta N"` — pid file existe mas para outra porta.
- `"processo PID X não encontrado"` — pid file órfão (servidor foi encerrado por outro meio).
- `"falha ao encerrar processo PID X"` — sem permissão (Linux/macOS) ou erro de signal.

---

### 5.6. `run` — Atalho start + comando + stop

Combina `start`, um comando de operação (`sign` ou `validate`) e `stop` em
uma única invocação. Útil para CI ou uso one-shot, onde não se quer deixar
servidor em background.

**Sintaxe:**
```bash
assinatura run <sign|validate> [flags do subcomando] [flags do start]
```

**Exemplos:**

```bash
assinatura run sign --content "documento temporário"
assinatura run validate --content "documento" --signature "..."
```

**Observação:** este comando é, na maior parte dos casos, equivalente a
`sign --local`. A diferença é que `run` sobe um servidor dedicado, executa
o comando via HTTP e encerra — útil se você estiver investigando um bug
específico do modo servidor.

---

## 6. Onde ficam os arquivos de estado

| Arquivo | Caminho | Quem escreve | Quem lê |
|---|---|---|---|
| `assinador.pid` | `~/.hubsaude/assinador.pid` | `assinatura start` | `assinatura sign`, `assinatura validate`, `assinatura stop` |
| `assinador.jar` | `~/.hubsaude/assinador.jar` | provisionador (download) | CLI e JAR |
| `JDK` provisionado | `~/.hubsaude/jdk/` | provisionador | `java` invocado pela CLI |

## 7. Variáveis de ambiente

| Variável | Consumida por | Efeito | Default |
|---|---|---|---|
| `HUBSAUDE_TIMEOUT_MINUTES` | `assinador.jar` (modo servidor) | Janela de auto-shutdown em minutos. Ausente ou `0` = desativado. | desativado |

---

## 8. Exemplos Práticos

### 8.1. Exemplo 1: Assinar um documento pontual

```bash
# Modo local, sem deixar servidor em background
assinatura sign --content "Conteúdo do contrato entre as partes..." --local

# Anotar a assinatura retornada
# (Exemplo: RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7)

# Validar a assinatura mais tarde
assinatura validate \
  --content "Conteúdo do contrato entre as partes..." \
  --signature "RUNNER_SIM_SIG_7c9e6679-7425-40de-944b-e07fc1f90ae7"
```

### 8.2. Exemplo 2: Múltiplas operações (modo servidor)

```bash
# Sobe o servidor uma vez
assinatura start

# Várias operações com baixa latência
assinatura sign --content "documento A"
assinatura sign --content "documento B"
assinatura validate --content "documento A" --signature "RUNNER..."

# Encerrar quando terminar
assinatura stop
```

### 8.3. Exemplo 3: Pipeline em script

```bash
#!/bin/bash
set -e

# Assina cada item de uma lista
for doc in "relatório 1" "relatório 2" "relatório 3"; do
  echo "Assinando: $doc"
  assinatura sign --content "$doc" --local
done
```

### 8.4. Exemplo 4: Capturando saída programaticamente

```bash
# Modo servidor para baixa latência
assinatura start

# Capturar saída em JSON (via HTTP) para processamento
# (o formato JSON depende do retorno do servidor, atualmente textual)
RESULT=$(assinatura sign --content "doc.txt")
SIG=$(echo "$RESULT" | awk -F': ' '/^Assinatura:/ {print $2}')
echo "Signature capturada: $SIG"

# Validar
assinatura validate --content "doc.txt" --signature "$SIG"

assinatura stop
```

---

## 9. Tratamento de Erros

A CLI Runner retorna **exit codes** padronizados para indicar sucesso ou falha:

| Código | Significado | Quando ocorre |
|--------|-------------|---------------|
| `0` | Sucesso | Operação concluída com êxito |
| `1` | Erro genérico | Erro não categorizado |
| `2` | Uso incorreto | Comando ou flag desconhecida (Cobra) |
| `64` | Argumentos inválidos | Flag obrigatória ausente ou formato incorreto |
| `65` | Dados inválidos | Assinatura inválida, parâmetros FHIR rejeitados |
| `70` | Erro interno | Falha de comunicação, exceção não tratada |
| `75` | Falha temporária | Health check falhou, JAR não respondeu no startup |
| `78` | Erro de configuração | Plataforma não suportada, arquivo de configuração inválido |
| `130` | Interrompido pelo usuário | Ctrl+C durante operação |

### 9.1. Mensagens comuns

**"missing required flag: --content"**
- Solução: inclua a flag `--content <texto>` no comando `sign` ou `validate`.

**"nenhum servidor registrado"**
- Causa: o pid file (`~/.hubsaude/assinador.pid`) não existe.
- Solução: rode `assinatura start` para criar o registro novamente.

**"Java não disponível"**
- Causa: nem `java` no PATH nem JDK provisionado.
- Solução: instale Java 21+ manualmente (e exporte `JAVA_HOME`), ou
  verifique sua conexão de internet para o provisionamento automático.

**"servidor não respondeu após 30 segundos"**
- Causa: o `java -jar` foi iniciado mas o health check falhou.
- Solução: confira se a porta não está tomada por outro processo
  (`netstat -an | grep 8080`), ou se o JAR não está corrompido.

**"assinador.jar não encontrado e falha no download automático"**
- Causa: o JAR não está em nenhum dos locais padrão e o download falhou.
- Solução: rode `mvn package` dentro de `projetos/assinador-java/` (modo
  desenvolvimento), ou coloque o JAR manualmente em `~/.hubsaude/assinador.jar`.

---

## 10. Perguntas Frequentes (FAQ)

### 10.1. Preciso ter Java instalado?

**Não necessariamente.** Na primeira execução, o Sistema Runner baixa o JDK automaticamente. Se você já tem JDK 21+ via `JAVA_HOME`, ele usa esse.

### 10.2. A assinatura gerada é real (criptograficamente válida)?

**Não.** Este sistema **simula** operações de assinatura digital. Use apenas para testes, desenvolvimento, ou estudos. Não use para documentos legais.

### 10.3. Posso usar em ARM (Apple Silicon, Raspberry Pi)?

Atualmente não. Apenas arquitetura amd64 é suportada oficialmente.

### 10.4. Onde fica armazenado o JDK baixado?

- **Linux/macOS:** `~/.hubsaude/jdk/`
- **Windows:** `%USERPROFILE%\.hubsaude\jdk\`

Para reinstalar, basta deletar esse diretório.

### 10.5. Como desinstalar?

1. Delete o binário (`assinatura` ou `assinatura.exe`)
2. Delete o diretório `~/.hubsaude/` (remove JDK provisionado, JAR cacheado, pid file)

### 10.6. Como atualizar para uma nova versão?

Baixe a versão mais recente da [página de Releases](https://github.com/jannder1/runner/releases) e substitua o binário antigo. Configurações e JDK em cache são preservados.

### 10.7. Qual a diferença entre `sign` e `run sign`?

- `sign` usa o servidor em background se ele existir; senão, vai de subprocess.
- `run sign` sempre sobe um servidor dedicado, executa o comando via HTTP, e encerra.

Na prática, `sign --local` é quase equivalente a `run sign` (a diferença é o
caminho de execução: subprocess vs HTTP).

### 10.8. Como sei se o servidor está rodando?

```bash
# Lê o pid file e testa o health check
cat ~/.hubsaude/assinador.pid   # se não existir, não está rodando
curl http://localhost:8080/health   # se retornar UP, está rodando
```

(Os comandos acima são bash/Linux. No Windows, use `type` em vez de `cat`.)

---

## 11. Onde Buscar Ajuda

- **Issues/Bugs:** [github.com/jannder1/runner/issues](https://github.com/jannder1/runner/issues)
- **Discussões:** [github.com/jannder1/runner/discussions](https://github.com/jannder1/runner/discussions)
- **Documentação técnica:** [`docs/02-design.md`](./02-design.md)
- **Decisões arquiteturais:** [`docs/adr/`](./adr/)

---

## 12. Glossário Rápido

| Termo | Significado |
|-------|-------------|
| **CLI** | Interface de linha de comandos (você digita comandos no terminal) |
| **FHIR** | Padrão de dados de saúde (define formato dos parâmetros) |
| **JDK** | Java Development Kit (necessário para rodar o Assinador) |
| **JAR** | Java ARchive — pacote executável Java |
| **pid file** | Arquivo que registra o PID de um processo em background |
| **Assinatura digital** | Operação criptográfica que prova autenticidade de documento |
| **Simulação** | Reprodução do comportamento sem operação real (não usa criptografia) |
| **Exit code** | Número retornado pelo comando ao sistema operacional indicando sucesso/falha |
| **Spring Boot** | Framework Java usado pelo `assinador.jar` |
| **Cobra** | Framework Go usado pela CLI `assinatura` |

---

**Versão do manual:** 2.0 (Julho/2026)
**Compatível com:** Sistema Runner v0.2.0+

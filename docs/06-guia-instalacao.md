# Guia de Instalação — Sistema Runner

> **Versão:** 1.0
> **Público-alvo:** Usuários finais e administradores de sistema

---

## 1. Visão Geral

Este guia descreve como instalar e configurar o **Sistema Runner** em diferentes plataformas.

**Tempo estimado:** 5-10 minutos (incluindo download e provisionamento de JDK na primeira execução).

---

## 2. Requisitos de Sistema

### 2.1. Mínimos

| Item | Requisito |
|------|-----------|
| SO | Windows 10/11, Ubuntu 20.04+, macOS 11+ |
| Arquitetura | amd64 (x86_64) |
| RAM | 256 MB disponíveis |
| Disco | 200 MB livres (incluindo JDK provisionado) |
| Internet | Necessária apenas na 1ª execução (download do JDK) |

### 2.2. Opcionais (mas úteis)

- **JDK 21+ pré-instalado**: se já tiver, o Runner detecta e usa, sem precisar baixar
- **PATH configurado**: para usar `assinatura` de qualquer diretório
- **Shell Unix-like**: bash, zsh, fish (para usuários Linux/macOS)

---

## 3. Instalação por Plataforma

### 3.1. Windows (amd64)

#### Passo 1: Download

1. Acesse [github.com/jannder1/runner/releases](https://github.com/jannder1/runner/releases)
2. Baixe `assinatura-windows-amd64.exe`
3. Verifique o SHA256 (veja seção 4)

#### Passo 2: Renomear (opcional)

Renomeie para `assinatura.exe` para facilitar o uso:

```powershell
Rename-Item assinatura-windows-amd64.exe assinatura.exe
```

#### Passo 3: Mover para diretório permanente

```powershell
# Criar diretório (se não existir)
New-Item -ItemType Directory -Force -Path "C:\Program Files\assinatura"

# Mover o executável
Move-Item assinatura.exe "C:\Program Files\assinatura\assinatura.exe"
```

#### Passo 4: Adicionar ao PATH

1. Pressione `Win + R`, digite `sysdm.cpl` e pressione Enter
2. Clique em **"Variáveis de Ambiente"**
3. Em **"Variáveis do usuário"**, selecione `Path` e clique em **"Editar"**
4. Clique em **"Novo"** e adicione: `C:\Program Files\assinatura`
5. Clique em **"OK"** em todas as janelas
6. **Reinicie o terminal** para carregar a nova variável

#### Passo 5: Verificar

Abra um novo PowerShell ou CMD:

```powershell
assinatura version
```

Saída esperada:
```
assinatura version 0.1.0
```

---

### 3.2. Linux (amd64)

#### Passo 1: Download

```bash
cd ~/Downloads
wget https://github.com/jannder1/runner/releases/download/v0.1.0/assinatura-linux-amd64
```

Ou via `curl`:

```bash
curl -L -o assinatura-linux-amd64 https://github.com/jannder1/runner/releases/download/v0.1.0/assinatura-linux-amd64
```

#### Passo 2: Tornar executável

```bash
chmod +x assinatura-linux-amd64
```

#### Passo 3: Mover para PATH

```bash
sudo mv assinatura-linux-amd64 /usr/local/bin/assinatura
```

#### Passo 4: Verificar

```bash
assinatura version
```

#### Alternativa: instalar localmente (sem sudo)

```bash
mkdir -p ~/.local/bin
mv assinatura-linux-amd64 ~/.local/bin/assinatura

# Adicionar ao PATH (adicione ao seu ~/.bashrc ou ~/.zshrc):
export PATH="$HOME/.local/bin:$PATH"

# Recarregar shell
source ~/.bashrc

# Testar
assinatura version
```

---

### 3.3. macOS (amd64)

#### Passo 1: Download

```bash
cd ~/Downloads
curl -L -o assinatura-darwin-amd64 https://github.com/jannder1/runner/releases/download/v0.1.0/assinatura-darwin-amd64
```

#### Passo 2: Tornar executável e mover

```bash
chmod +x assinatura-darwin-amd64
sudo mv assinatura-darwin-amd64 /usr/local/bin/assinatura
```

#### Passo 3: Lidar com Gatekeeper (Apple Silicon e versões recentes)

Se aparecer erro de "desenvolvedor não identificado":

```bash
# Remover atributo de quarentena
xattr -d com.apple.quarantine /usr/local/bin/assinatura

# Ou permitir via Preferências do Sistema:
# Preferências do Sistema > Segurança e Privacidade > "Abrir mesmo assim"
```

#### Passo 4: Verificar

```bash
assinatura version
```

---

## 4. Verificação de Integridade (SHA256)

### 4.1. Por que verificar?

A verificação garante que o arquivo baixado não foi corrompido durante o download e não foi adulterado.

### 4.2. Como verificar

**Linux / macOS:**

```bash
# Baixar arquivo de checksums
wget https://github.com/jannder1/runner/releases/download/v0.1.0/SHA256SUMS

# Verificar
sha256sum -c SHA256SUMS
```

Saída esperada:
```
assinatura-linux-amd64: OK
```

**Windows (PowerShell):**

```powershell
# Gerar hash do arquivo baixado
Get-FileHash assinatura-windows-amd64.exe -Algorithm SHA256

# Comparar com o valor em SHA256SUMS
```

Exemplo de saída:
```
Algorithm  Hash                                                              Path
---------  ----                                                              ----
SHA256     A1B2C3D4E5F6...                                                   assinatura-windows-amd64.exe
```

Compare manualmente com o valor em `SHA256SUMS`.

---

## 5. Configuração do JDK

### 5.1. Provisionamento Automático (padrão)

Na primeira execução, o Sistema Runner detecta que não há JDK e **baixa automaticamente** o Eclipse Temurin 21 LTS:

```bash
$ assinatura version
Detectando JDK...
JDK não encontrado. Baixando Eclipse Temurin 21 LTS...
Baixando: https://api.adoptium.net/.../jdk-21.tar.gz
[################################################] 100%
Extraindo JDK para ~/.runner/jdk/...
Configurando JAVA_HOME...
JDK provisionado com sucesso em ~/.runner/jdk/

assinatura version 0.1.0
```

**Nas próximas execuções**, esse processo é pulado (cache em `~/.runner/jdk/`).

### 5.2. Usar JDK do sistema (alternativa)

Se você já tem JDK 21+ instalado:

```bash
# Definir JAVA_HOME (Linux/macOS)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Verificar
java -version
```

**Windows:**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

Para tornar permanente, configure nas variáveis de ambiente do sistema.

### 5.3. Modo offline

Se você não quer que o Runner baixe JDK automaticamente:

```bash
assinatura --offline version
```

O Runner usará o JDK do `JAVA_HOME` ou do `PATH`. Se nenhum for encontrado, retornará erro com exit code 78.

---

## 6. Desinstalação

### 6.1. Remover o binário

**Linux / macOS:**
```bash
sudo rm /usr/local/bin/assinatura
```

**Windows:**
- Delete `C:\Program Files\assinatura\assinatura.exe`
- Remova o diretório do PATH (se adicionou)

### 6.2. Remover JDK provisionado e configurações

**Linux / macOS:**
```bash
rm -rf ~/.runner/
```

**Windows:**
```powershell
Remove-Item -Recurse -Force "$env:USERPROFILE\.runner"
```

### 6.3. Verificação

```bash
# Confirmar que o binário foi removido
which assinatura
# (não deve retornar nada)

# Confirmar que configurações foram removidas
ls ~/.runner/
# (deve retornar "No such file or directory")
```

---

## 7. Atualização

Para atualizar para uma nova versão:

1. Baixe a versão mais recente em [Releases](https://github.com/jannder1/runner/releases)
2. Substitua o binário antigo pelo novo
3. **Não é necessário** reinstalar JDK ou configurações (são preservados em `~/.runner/`)

```bash
# Linux/macOS: atualizar manualmente
sudo mv ~/Downloads/assinatura-linux-amd64 /usr/local/bin/assinatura

# Verificar nova versão
assinatura version
# assinatura version 0.2.0
```

---

## 8. Solução de Problemas (Troubleshooting)

### 8.1. "permission denied" no Linux/macOS

**Causa:** binário não tem permissão de execução.

**Solução:**
```bash
chmod +x /usr/local/bin/assinatura
```

### 8.2. "comando não encontrado" após instalar

**Causa:** PATH não foi atualizado ou terminal não foi reiniciado.

**Solução:**
- **Linux/macOS:** `source ~/.bashrc` ou reinicie o terminal
- **Windows:** reinicie o PowerShell/CMD

### 8.3. "Falha ao baixar JDK"

**Causa:** sem conexão com internet ou firewall bloqueando.

**Soluções:**
1. Verifique sua conexão: `curl https://api.adoptium.net`
2. Se estiver atrás de proxy corporativo, configure `HTTPS_PROXY`
3. Como alternativa, instale JDK manualmente e use `--offline`

### 8.4. "Plataforma não suportada"

**Causa:** arquitetura diferente de amd64 (ex: ARM).

**Solução:** instale manualmente em plataforma amd64 (não há suporte oficial para ARM nesta versão).

### 8.5. "Porta 8080 já está em uso"

**Causa:** outro processo está usando a porta que o Assinador precisa.

**Solução:**
```bash
# Verificar o que está usando a porta 8080
# Linux/macOS:
lsof -i :8080

# Windows:
netstat -ano | findstr :8080

# Parar o processo conflitante ou usar outra porta (futura feature)
```

### 8.6. macOS: "assinatura não pode ser aberto porque o desenvolvedor não pode ser verificado"

**Causa:** Gatekeeper do macOS.

**Solução:**
```bash
xattr -d com.apple.quarantine /usr/local/bin/assinatura
```

Ou via Preferências do Sistema > Segurança e Privacidade.

---

## 9. Verificação Final

Após a instalação, execute esta sequência para validar:

```bash
# 1. Versão
assinatura version

# 2. Ajuda geral
assinatura --help

# 3. Ajuda de subcomando
assinatura sign --help

# 4. Teste end-to-end (cria arquivo temporário, assina, valida)
echo "teste" > /tmp/doc.txt
assinatura sign create --input /tmp/doc.txt --cert-id teste-001

# (Anote o signatureValue retornado)
assinatura sign validate --signature "RUNNER_SIM_SIG_..."

# 5. Limpeza
rm /tmp/doc.txt
```

Se todos os comandos retornarem exit code 0, a instalação foi bem-sucedida.

---

## 10. Próximos Passos

- 📖 Leia o [Manual de Usuário](05-manual-usuario.md) para aprender a usar todos os comandos
- 🏗️ Consulte a [Documentação Técnica](07-integracao.md) para entender a arquitetura
- 🧪 Veja os [Critérios de Aceitação](../tests/bdd/features/) executáveis

---

**Versão do guia:** 1.0
**Compatível com:** Sistema Runner v0.1.0+
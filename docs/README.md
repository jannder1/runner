Sistema Runner
CLI multiplataforma para simulação de assinatura digital — Trabalho prático de Implementação e Integração (2026-01), Bacharelado em Engenharia de Software — UFG.

Professor: Dr. Fabio Lucena

O que é isso?
O Sistema Runner é uma CLI multiplataforma que abstrai a complexidade de executar aplicações Java relacionadas a assinatura digital simulada. O sistema é composto por:

CLI Runner (assinatura): binário Go multiplataforma que o usuário final invoca
Assinador (assinador.jar): aplicação Java que valida parâmetros FHIR e simula operações de assinatura
⚠️ Este sistema simula operações de assinatura digital. Não é adequado para uso em produção com dados reais.

Instalação Rápida
Baixe o binário pré-compilado para sua plataforma na página de Releases:

Plataforma	Binário
Windows (amd64)	assinatura-windows-amd64.exe
Linux (amd64)	assinatura-linux-amd64
macOS (amd64)	assinatura-darwin-amd64
Verificação de integridade
Após o download, verifique o checksum SHA256:

bash

Copy
# Linux / macOS

sha256sum -c SHA256SUMS


# Windows (PowerShell)

Get-FileHash assinatura-windows-amd64.exe -Algorithm SHA256
Uso Básico
bash

Copy
# Exibir versão

assinatura version


# Criar assinatura simulada

assinatura sign create --input documento.txt --cert-id cert-001


# Validar assinatura simulada

assinatura sign validate --signature "RUNNER_SIM_SIG_..."


# Gerenciar Simulador HubSaúde

assinatura simulator start

assinatura simulator status

assinatura simulator stop


# Ajuda

assinatura --help

assinatura sign --help
💡 Na primeira execução, o sistema provisiona automaticamente o JDK necessário.

Estrutura do Repositório
text

Copy
runner/

├── docs/                          # Documentação do projeto

│   ├── 01-especificacao.md        # Especificação de requisitos

│   ├── 02-design.md               # Arquitetura e decisões (C4)

│   ├── 03-plano-implementacao.md  # Plano de sprints

│   └── 04-backlog/                # Export de issues (opcional)

├── projetos/

│   ├── assinatura/                # CLI Runner (Go + Cobra)

│   └── assinador-java/            # Assinador (Java 21)

├── diagramas/                     # Diagramas C4 (SVG + fonte Mermaid)

├── .github/

│   ├── workflows/                 # CI/CD

│   └── ISSUE_TEMPLATE/            # Templates de issues

└── README.md                      # Este arquivo
Documentação Completa
📋 Especificação — requisitos, escopo, casos de uso
🏗️ Design — arquitetura, C4, ADRs, contratos
📅 Plano de Implementação — sprints, tasks, DoR/DoD
🎯 Diagramas C4 — contexto e contêineres
Build Local (Desenvolvedores)
CLI Runner (Go)
bash

Copy
cd projetos/assinatura

go mod download

go build -o bin/assinatura ./cmd/assinatura

./bin/assinatura version
Assinador (Java)
bash

Copy
cd projetos/assinador-java

mvn clean package

java -jar target/assinador.jar
Testes
bash

Copy
# CLI

cd projetos/assinatura && go test ./...


# Assinador

cd projetos/assinador-java && mvn test
Contribuindo
1.
Crie uma issue usando os templates disponíveis
2.
Siga a Definition of Ready e Definition of Done
3.
Use Conventional Commits nas mensagens
4.
Abra um Pull Request linkando a issue correspondente
Licença


Contato
Disciplina: Implementação e Integração (2026-01)
Professor: Dr. Fabio Lucena — UFG
Equipe: @jannderson @thamaraprata

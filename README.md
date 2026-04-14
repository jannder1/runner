# runner
Repositório para construção de software de assinatura digital simulada, referente à disciplina de Integração e Implementação de Software, professor Dr. Fabio Lucena - UFG.

# Sistema de Assinatura Digital - Trabalho Prático

## 1. Visão Geral

Este documento define o trabalho prático da disciplina de Implementação e Integração do Bacharelado em Engenharia de Software (2026). O objetivo deste projeto é proporcionar aos estudantes experiência prática no desenvolvimento de um sistema de software por meio da construção de um **Sistema de Assinatura Digital**, voltado para simulação de processos de assinatura e validação de documentos digitais.

O sistema proposto permite a integração com aplicações externas e simula operações típicas de assinatura digital, sendo útil para estudos de interoperabilidade, validação de dados e integração entre sistemas distribuídos.

---

## 2. Objetivo do Sistema de Assinatura Digital

Fornecer uma solução simplificada para simulação de assinatura e validação de documentos digitais por meio de uma interface de linha de comando (CLI), abstraindo a complexidade de configuração de ambiente e processos criptográficos reais.

---

## 3. Objetivos Específicos

1. Permitir que usuários realizem operações de assinatura digital sem necessidade de conhecimento técnico avançado em criptografia ou configuração de ambiente.

2. Disponibilizar uma interface de linha de comando (CLI) simples e intuitiva, possibilitando a execução de comandos de assinatura e validação.

3. Simular o comportamento de sistemas reais de assinatura digital, incluindo validação rigorosa de parâmetros de entrada.

---

## 4. Escopo

### 4.1. O que ESTÁ no Escopo

- Desenvolvimento da aplicação **assinatura** (CLI multiplataforma)  
- Desenvolvimento da aplicação **assinador.jar** (Java)  
- Integração entre CLI e assinador  
- Validação rigorosa de parâmetros  
- Simulação de criação de assinatura digital  
- Simulação de validação de assinatura digital  
- Tratamento de erros e exceções  
- Desenvolvimento de testes (unitários e integração)  
- Documentação de uso  

### 4.2. O que NÃO ESTÁ no Escopo

- Implementação real de criptografia  
- Integração com autoridades certificadoras (AC)  
- Emissão de certificados digitais  
- Armazenamento persistente de assinaturas  
- Interface gráfica (GUI)  
- Autenticação e autorização de usuários  

---

## 5. Requisitos Funcionais

### US-01: Executar operações via CLI

Como usuário do sistema  
Quero executar comandos de assinatura e validação via linha de comando  
Para que eu possa utilizar o sistema de forma simples e direta  

### US-02: Simular assinatura digital

Como usuário  
Quero que o sistema valide parâmetros antes de simular uma assinatura  
Para que eu receba feedback sobre possíveis erros  

### US-03: Simular validação de assinatura

Como usuário  
Quero validar uma assinatura digital simulada  
Para que eu possa verificar sua autenticidade  

---

## 6. Arquitetura e Integração

A aplicação CLI (**assinatura**) se comunica com o **assinador.jar** por dois modos:

- Modo local: execução direta via linha de comando  
- Modo servidor: comunicação via HTTP  

---

## 7. Entregáveis

- Código-fonte da CLI  
- Código-fonte do assinador.jar  
- Testes unitários e de integração  
- Documentação completa  
- Binários multiplataforma  

---

## 8. Considerações de Implementação

- Assinaturas são simuladas (mock)  
- Validação retorna resultados simulados  
- Foco principal: validação de parâmetros  

---

## 9. Segurança e Integridade

Os artefatos distribuídos devem ser assinados e verificáveis.

---

## 10. Referências

- Boas práticas de CLI  
- Segurança em software  
- Swebook
  

# Regras do projeto

Este arquivo é a fonte principal das regras para agentes que atuam neste projeto.

## Objetivo do serviço

Construir uma API de pedidos, com regras de negócio claras, testáveis e independentes de frameworks. O escopo funcional detalhado de cada mudança deve ser obtido na spec indicada para a tarefa.

## Stack e comandos

- Usar Java 21.
- Usar Spring Boot para a borda da aplicação e a infraestrutura necessária.
- Executar os comandos do projeto pelo Maven Wrapper: `./mvnw` em ambientes Unix ou `mvnw.cmd` no Windows.
- Não alterar `pom.xml` nem adicionar dependências sem pedir e receber autorização explícita.

## Arquitetura

- Organizar o código seguindo arquitetura hexagonal (ports and adapters).
- Manter o domínio e a aplicação independentes de Spring, JPA e demais detalhes de infraestrutura.
- Colocar integrações, persistência, configuração e transporte nos adaptadores apropriados.
- Não deixar anotações, interfaces ou tipos de framework vazarem para o domínio ou para os casos de uso da aplicação.

## Regras de implementação

- Representar valores monetários com `BigDecimal`, evitando `double` e `float` para dinheiro.
- Não usar Lombok.
- Preferir código explícito, imutabilidade quando fizer sentido e nomes que expressem o domínio.
- Não criar infraestrutura antes de haver necessidade definida pela tarefa e pela spec.

## Fluxo obrigatório de trabalho

Antes de implementar qualquer mudança:

1. Ler a spec indicada para a tarefa.
2. Inspecionar o projeto e as partes relevantes do código.
3. Propor um plano curto, identificando arquivos e decisões principais.
4. Aguardar o OK do usuário.

Depois do OK:

1. Implementar somente o escopo aprovado.
2. Executar os testes apropriados usando o Maven Wrapper.
3. Revisar o resultado e verificar que as regras arquiteturais foram preservadas.
4. Mostrar o diff e informar os testes executados e seus resultados.

Se não houver spec indicada, informar isso e pedir orientação antes de implementar comportamento de negócio.


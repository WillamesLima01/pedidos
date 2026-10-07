# Caso de uso: criar pedido

## Contexto

O domínio de pedido já representa `Pedido`, `ItemPedido` e `StatusPedido` sem depender de Spring, JPA ou outros detalhes externos. O próximo passo é adicionar uma ação da aplicação que receba um cliente e seus itens, crie um pedido e delegue o salvamento a uma porta de saída.

O caso de uso deve permitir testes unitários sem Spring, HTTP, banco de dados ou adapters reais.

## Tarefa

Criar o caso de uso `CriarPedido` como port de entrada, sua implementação `CriarPedidoService` e o port de saída `Pedidos`.

O contrato comportamental é:

- Receber um cliente e uma lista de itens.
- Recusar uma lista de itens vazia.
- Criar um pedido do domínio com o cliente e os itens recebidos.
- Salvar o pedido usando a interface `Pedidos`.
- Devolver exatamente o pedido retornado por `Pedidos`.

A aplicação deve ficar em um pacote próprio e não pode importar Spring, JPA, HTTP, controllers, DTOs, configuração ou adapters.

O teste do caso de uso deve fornecer uma implementação em memória de `Pedidos` dentro do próprio teste.

## Regras

### Regras do caso de uso

- A lista de itens deve ser obrigatória e não pode estar vazia.
- Uma lista vazia deve ser recusada antes de salvar qualquer pedido.
- O cliente recebido deve ser encaminhado ao pedido criado conforme a representação definida para o domínio.
- Todos os itens recebidos devem ser encaminhados ao pedido criado.
- O pedido criado deve respeitar as regras da spec 01: estado inicial `ABERTO`, total derivado e itens válidos.
- O port `Pedidos` deve ser usado para salvar o pedido.
- O resultado do caso de uso deve ser o pedido devolvido por `Pedidos`, inclusive quando a implementação de saída devolver uma instância diferente.
- O caso de uso não deve conhecer banco de dados, HTTP, Spring, JPA ou qualquer adapter.

### Regras arquiteturais

- `CriarPedido` é um port de entrada.
- `CriarPedidoService` implementa o port de entrada e coordena o fluxo.
- `Pedidos` é um port de saída.
- As interfaces dos ports pertencem à aplicação.
- A aplicação depende do domínio e das abstrações dos ports.
- A aplicação não depende de adapters ou frameworks.
- Não criar Controller, adapter JPA, DTO, configuração ou commit nesta mudança.

## Definição de pronto

Os testes devem cobrir:

- Criar um pedido com cliente e uma lista de itens válidos.
- Verificar que o pedido criado começa `ABERTO`.
- Verificar que todos os itens recebidos chegam ao pedido.
- Verificar que uma lista vazia é recusada.
- Verificar que a implementação em memória de `Pedidos` não é chamada quando a lista está vazia.
- Verificar que o port `Pedidos` recebe o pedido criado.
- Configurar `Pedidos` para devolver outro pedido e verificar que o caso de uso devolve exatamente essa instância.
- Verificar que o caso de uso não exige Spring, JPA, HTTP ou banco para ser testado.
- Verificar, por inspeção dos imports, que `application/` não importa Spring, JPA, HTTP ou adapters.
- Executar os testes pelo Maven Wrapper.
- Mostrar os imports de `application/` e o diff final.

### Ambiguidade que precisa ser resolvida antes da implementação

A spec 01 atual define `Pedido` com UUID, status e itens, mas não possui cliente. Esta spec exige que o caso de uso receba e crie o pedido com cliente, porém não define:

- o tipo do cliente (`String`, value object ou outro tipo);
- se o cliente deve ser adicionado ao estado de `Pedido`;
- se alterar `Pedido.java` e seus testes faz parte desta mudança.

O plano de implementação deve explicitar essa decisão antes do OK. Não é correto receber o cliente e descartá-lo silenciosamente.


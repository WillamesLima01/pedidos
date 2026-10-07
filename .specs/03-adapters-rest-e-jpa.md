# Checkpoint 4B — adapters REST e JPA

## Contexto

O domínio e a aplicação já existem sem dependências de Spring, JPA ou HTTP. O Postgres 16 está disponível por `infra/docker-compose.yml`, com banco, usuário e senha `pedidos`, e porta local configurável.

Este checkpoint adiciona apenas a infraestrutura de persistência e a configuração necessária para provar a integração. O adapter REST será preparado conceitualmente, mas nenhum endpoint será criado nesta etapa.

## Tarefa

Implementar o adapter de persistência JPA para a porta de saída `Pedidos`, mantendo domínio e aplicação intactos.

Criar:

- `PedidoJpaEntity` para mapear a tabela `pedido`;
- `ItemJpaEntity` para mapear os itens do pedido;
- um repository Spring Data;
- um mapper manual entre domínio e entidades JPA;
- `PedidosJpaAdapter`, que implementa `Pedidos`.

Configurar o acesso ao Postgres local em `application.yml`, com `open-in-view=false`.

Adicionar ao `pom.xml` somente as dependências necessárias de Data JPA e PostgreSQL Driver. Se elas já estiverem presentes, não duplicá-las nem adicionar outras dependências.

Não criar endpoint REST, controller, DTO, H2, MapStruct, Lombok ou configuração adicional fora do necessário.

## Regras

### Persistência

- A entidade do pedido deve mapear a tabela `pedido`.
- O identificador persistido deve ser exatamente o UUID do domínio, representado como `String` ou `UUID`, sem gerar outro identificador.
- Cada item deve guardar `sku`, `quantidade` e `precoUnitario`.
- O total não deve ser persistido em coluna; deve continuar sendo derivado pelo domínio.
- O status deve ser persistido e restaurado como `ABERTO`, `PAGO` ou `CANCELADO`.
- O cliente deve ser persistido conforme o estado atual de `Pedido`.
- O relacionamento entre pedido e itens deve permitir recuperar os itens durante o mapeamento.
- O mapper deve ser manual, explícito e sem MapStruct.
- O adapter deve usar transação para que os itens sejam mapeados antes do encerramento da sessão JPA.

### Dependências e arquitetura

- Domínio e aplicação permanecem sem imports de Spring, JPA, HTTP ou adapters.
- Apenas adapters, entidades e configuração conhecem Spring Data JPA e PostgreSQL.
- `PedidosJpaAdapter` implementa o port de saída `Pedidos`.
- Nenhum endpoint REST deve ser criado neste checkpoint.

### Três provas obrigatórias

1. **Banco disponível**
   - O Postgres local deve estar em execução pelo Compose.
   - O teste deve conseguir abrir conexão com o banco configurado.

2. **Persistência pelo adapter**
   - Em uma integração explícita, salvar o pedido de `c-1` com `CAFE-500`, quantidade `2` e preço `18.90`.
   - Recuperar o pedido por seu UUID em uma nova transação.
   - Verificar cliente, itens, status `ABERTO` e total derivado `37.80`.

3. **HTTP**
   - Registrar que o contexto Spring sobe com a infraestrutura configurada.
   - O teste de contexto deve seguir a convenção `*IT` e execução explícita.
   - Não criar endpoint ainda; a prova HTTP completa ficará para um checkpoint posterior.

### Testes e execução

- Preservar os testes unitários existentes.
- Adaptar o teste de contexto gerado para a convenção `*IT`; não apagá-lo nem desativá-lo.
- Criar `PedidosJpaAdapterIT`.
- Os testes unitários devem rodar pelo Maven Wrapper sem exigir banco.
- Os testes `*IT` devem ser executados explicitamente contra o Postgres local.
- A execução deve mostrar a consulta à tabela `pedido` usada para comprovar a persistência.
- Não remover containers ou volumes alheios.

## Definição de pronto

- O arquivo de dependências contém somente Data JPA e PostgreSQL Driver como adições, sem duplicações.
- O domínio e a aplicação permanecem intactos e sem imports de framework.
- `application.yml` configura o Postgres local e `spring.jpa.open-in-view=false`.
- `PedidoJpaEntity`, `ItemJpaEntity`, repository, mapper manual e `PedidosJpaAdapter` existem.
- O UUID persistido é o mesmo UUID criado pelo domínio.
- Não existe coluna `total` na entidade/tabela do pedido.
- O pedido `c-1` é salvo e recuperado com `CAFE-500`, `2`, `18.90`, `ABERTO` e total `37.80`.
- A recuperação ocorre em nova transação e não depende de uma sessão aberta durante o mapeamento.
- Testes unitários passam sem banco.
- `PedidosJpaAdapterIT` e o teste de contexto `*IT` passam quando executados explicitamente com Postgres disponível.
- A consulta à tabela `pedido` é apresentada.
- O diff é apresentado.

## Contrato HTTP — checkpoint seguinte

Esta seção define o contrato REST a ser implementado depois da persistência. Ela não autoriza a criação de endpoint neste checkpoint JPA.

### Endpoint

`POST /pedidos`

O controller deve chamar o port de entrada `CriarPedido`. Ele não pode conhecer JPA, repository Spring Data ou entidade de persistência.

### Requisição

Content-Type: `application/json`

```json
{
  "clienteId": "c-1",
  "itens": [
    {
      "sku": "CAFE-500",
      "quantidade": 2,
      "precoUnitario": 18.90
    }
  ]
}
```

- `clienteId` é obrigatório e deve ser texto não vazio.
- `itens` é obrigatório e deve ser uma lista JSON.
- Cada item deve possuir `sku`, `quantidade` e `precoUnitario`.
- `quantidade` e `precoUnitario` devem ter formato válido para seus tipos.
- A validação de presença e formato usa `@Valid`.
- A regra de quantidade e preço positivos continua no domínio.
- Lista vazia é uma regra do caso de uso e deve resultar em `422`.

### Resposta de sucesso

Status: `201 Created`

```json
{
  "id": "uuid-do-pedido",
  "clienteId": "c-1",
  "itens": [
    {
      "sku": "CAFE-500",
      "quantidade": 2,
      "precoUnitario": 18.90
    }
  ],
  "status": "ABERTO",
  "total": 37.80
}
```

Preços e total devem ser serializados como números decimais JSON, não como strings.

### Erros

- `422 Unprocessable Entity` para `ItemInvalidoException`, com mensagem no corpo.
- `422 Unprocessable Entity` para `PedidoSemItensException`, com mensagem no corpo.
- `400 Bad Request` para JSON malformado.
- `400 Bad Request` para campos obrigatórios ausentes ou com formato inválido.

O `PedidoExceptionHandler`, em `adapters/entrada/rest`, será responsável por esses mapeamentos. Não criar outros endpoints.

### Provas HTTP

Após a implementação, iniciar a aplicação e registrar status e corpo para:

- `CAFE-500`, quantidade `2`, preço `18.90`: `201` e total `37.80`;
- quantidade zero: `422`;
- lista de itens vazia: `422`;
- JSON malformado: `400`.

Para as recusas, verificar no Postgres que nenhuma linha nova foi adicionada. Consultar o pedido criado pelo UUID, reiniciar a aplicação e verificar que a mesma linha e seus itens continuam persistidos.

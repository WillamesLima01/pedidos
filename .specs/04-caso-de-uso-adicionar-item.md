# Caso de uso: adicionar item ao pedido

## Contexto

O serviço já cria pedidos por `POST /pedidos` e persiste pedidos pelo port `Pedidos`. O próximo comportamento é adicionar um item a um pedido existente, preservando o UUID do pedido e recalculando o total a partir dos itens.

Esta mudança atravessa domínio, aplicação, persistência e REST, mas deve manter as dependências apontando para dentro. O caso de uso `CriarPedido` não deve ser alterado.

## Tarefa

Adicionar o caso de uso de adicionar item e expô-lo por:

```text
POST /pedidos/{id}/itens
```

O caso de uso deve:

- receber o UUID do pedido e os dados do novo item;
- buscar o pedido pelo port de saída `Pedidos`;
- lançar `PedidoNaoEncontradoException` quando não houver pedido para o UUID;
- recusar pedido fechado (`PAGO` ou `CANCELADO`);
- validar o item pelo domínio;
- adicionar o item devolvendo um novo estado de `Pedido`;
- preservar o mesmo UUID;
- atualizar o total de forma derivada;
- salvar o pedido atualizado pelo port `Pedidos`;
- devolver o pedido salvo.

O endpoint deve devolver `200 OK` com a representação completa do pedido atualizado.

O handler REST existente deve ser ampliado. Não alterar o caso de uso `CriarPedido` e não criar outros endpoints.

## Regras

### Aplicação e domínio

- O port `Pedidos` deve oferecer busca por UUID e salvamento.
- O caso de uso deve buscar antes de alterar.
- Pedido inexistente deve gerar `PedidoNaoEncontradoException`.
- Pedido `PAGO` ou `CANCELADO` deve ser considerado fechado.
- Pedido fechado não pode ser salvo novamente por esta operação.
- Quantidade zero ou negativa deve continuar sendo recusada pelo domínio.
- Preço zero ou negativo deve continuar sendo recusado pelo domínio.
- O novo pedido deve ter o mesmo UUID do pedido encontrado.
- O pedido original deve permanecer inalterado enquanto o novo estado é produzido.
- O novo total deve ser derivado dos itens.
- Nenhuma regra deve importar Spring ou JPA no domínio ou em `application/`.

### REST

Endpoint:

```text
POST /pedidos/{id}/itens
```

Requisição:

```json
{
  "sku": "CAFE-500",
  "quantidade": 1,
  "precoUnitario": 18.90
}
```

Resposta de sucesso:

- Status `200 OK`.
- Corpo no formato de `PedidoResponse`.
- UUID igual ao pedido buscado.
- Status preservado, normalmente `ABERTO`.
- Itens anteriores e novo item presentes.
- Total serializado como número decimal JSON.

Mapeamento de erros:

- `PedidoNaoEncontradoException` → `404 Not Found`.
- Pedido fechado → `409 Conflict`.
- `ItemInvalidoException` → `422 Unprocessable Entity`.
- JSON malformado ou campos obrigatórios ausentes/inválidos → `400 Bad Request`.

O `PedidoExceptionHandler` existente deve receber os novos mapeamentos. Erros não podem persistir alteração parcial.

### Caso verificável

Para um pedido existente com:

- UUID qualquer, preservado;
- status `ABERTO`;
- total atual `37.80`;
- item `CAFE-500`, quantidade `2`, preço `18.90`;

adicionar `1 x 18.90` deve produzir:

```text
total = 37.80 + 18.90 = 56.70
```

O pedido deve continuar com o mesmo UUID e status `ABERTO`.

## Definição de pronto

### Casos de teste unitário

- Buscar pedido existente pelo port `Pedidos`.
- Adicionar item válido a pedido `ABERTO`.
- Preservar o UUID do pedido.
- Atualizar o total de `37.80` para `56.70`.
- Salvar o novo estado pelo port `Pedidos`.
- Devolver o pedido retornado pelo port após o salvamento.
- Recusar UUID inexistente com `PedidoNaoEncontradoException`.
- Recusar adição a pedido `PAGO`.
- Recusar adição a pedido `CANCELADO`.
- Recusar quantidade zero.
- Recusar quantidade negativa.
- Recusar preço zero.
- Recusar preço negativo.
- Verificar que falhas não chamam o salvamento.
- Verificar que o pedido original não é mutado.
- Verificar que `application/` e `domain/` não possuem imports de Spring ou JPA.

### Casos de integração HTTP

- `POST /pedidos/{id}/itens` com item válido retorna `200` e total `56.70`.
- UUID da resposta é igual ao UUID da URL.
- Pedido inexistente retorna `404`.
- Pedido `PAGO` retorna `409`.
- Pedido `CANCELADO` retorna `409`.
- Quantidade zero retorna `422`.
- Preço negativo retorna `422`.
- JSON malformado retorna `400`.
- Campos obrigatórios ausentes retornam `400`.
- Consultar o banco antes e depois das recusas comprova que nenhuma linha de pedido ou item foi gravada.
- Consultar o pedido atualizado comprova o item e o total `56.70`.
- Executar os testes de unidade sem banco pelo Maven Wrapper.
- Executar o cenário HTTP contra o Postgres local usando os `*IT`.
- Mostrar o diff e os imports verificados.

### Limites de arquivos

Permitidos após aprovação:

- alterações no port `Pedidos` para suportar busca;
- novos ports/casos de uso de adicionar item em `application/`;
- alterações no `PedidoExceptionHandler` existente;
- novos tipos REST estritamente necessários para a requisição, resposta e exceções;
- novo teste unitário do caso de uso;
- novo teste `*IT` do endpoint;
- nenhuma alteração em `CriarPedido` ou no endpoint `POST /pedidos`.

Fora do escopo:

- novos endpoints além de `POST /pedidos/{id}/itens`;
- alterações em `pom.xml`, entidades JPA, mapper, configuração do banco ou Docker;
- alterações de comportamento do domínio não exigidas por esta tarefa;
- DTOs ou adapters não relacionados;
- commit.


# Domínio inicial de pedido

## Contexto

O serviço precisa de um núcleo de negócio para representar pedidos sem depender de transporte, persistência ou framework. Esta primeira mudança define o comportamento essencial de um pedido, seus itens e seu ciclo de vida, mantendo as regras independentes de Spring e JPA.

O exemplo de referência da aula é a compra de `CAFE-500`: 2 unidades a `18.90` resultam em total `37.80`.

## Tarefa

Definir o comportamento inicial do domínio com as entidades conceituais `Pedido`, `ItemPedido` e `StatusPedido`.

Um pedido deve:

- Ser criado como um rascunho novo, vazio, com um identificador único UUID e status `ABERTO` por meio de `Pedido.novo`.
- Aceitar itens somente enquanto estiver `ABERTO`.
- Ao adicionar um item, devolver outro objeto de pedido, preservando o pedido original.
- Calcular seu total a partir dos itens, como a soma de quantidade multiplicada pelo preço de cada item; o total não é um valor informado ou armazenado independentemente dos itens.
- Permitir pagamento quando estiver `ABERTO`, passando a `PAGO`.
- Permitir cancelamento quando estiver `ABERTO`, passando a `CANCELADO`.
- Recusar qualquer transição de status que não esteja explicitamente permitida.

Cada item deve representar um produto identificado por um código, uma quantidade e um preço unitário. O comportamento deve garantir que quantidade e preço sejam positivos.

O caso da aula deve ser representável: um pedido `ABERTO` contendo `CAFE-500`, quantidade `2` e preço unitário `18.90` deve ter total `37.80`.

## Regras

### Regras de negócio

- A quantidade de um item deve ser maior que zero.
- O preço unitário de um item deve ser maior que zero.
- Valores monetários devem usar `BigDecimal`, sem `double` ou `float`.
- O total do pedido deve ser derivado dos itens e deve refletir a soma exata de cada quantidade vezes seu preço unitário.
- Um pedido recém-criado deve estar `ABERTO` e não deve conter itens.
- Um pedido `ABERTO` pode ser pago e pode ser cancelado.
- Um pedido `PAGO` não pode receber itens nem ser cancelado ou pago novamente.
- Um pedido `CANCELADO` não pode receber itens nem ser pago ou cancelado novamente.
- Adicionar item a um pedido `ABERTO` deve devolver um novo objeto, deixando o pedido anterior inalterado.
- Tentar adicionar item a um pedido `PAGO` ou `CANCELADO` deve ser recusado.
- Tentar executar uma transição de status não permitida deve ser recusado.

### Regras de imutabilidade e encapsulamento

- O estado observável do domínio deve ser imutável.
- A coleção de itens exposta por um pedido não pode ser modificada por código externo.
- A criação de um pedido com itens e a devolução de itens devem usar cópia defensiva, para que alterações na coleção de entrada ou em referências externas não alterem o pedido.
- Usar `record` para as representações do domínio compatíveis com esse requisito, incluindo o item e os valores imutáveis necessários.
- `Pedido.novo` deve criar um UUID novo para cada pedido criado.
- O domínio não pode depender de Spring, JPA, anotações de persistência ou outros detalhes de infraestrutura.

### Convenções do exemplo

- Produto: `CAFE-500`.
- Quantidade: `2`.
- Preço unitário: `BigDecimal` representando `18.90`.
- Total esperado: `BigDecimal` representando `37.80`.

## Definição de pronto

Cada regra deve estar coberta por um caso de teste automatizado, incluindo os cenários de erro:

- Criar um pedido novo e verificar que ele possui UUID, está `ABERTO`, está vazio e tem total zero.
- Criar dois pedidos novos e verificar que seus UUIDs são diferentes.
- Criar um item com quantidade positiva e preço positivo e verificar seus dados.
- Recusar item com quantidade zero.
- Recusar item com quantidade negativa.
- Recusar item com preço zero.
- Recusar item com preço negativo.
- Adicionar um item a pedido `ABERTO` e verificar que o resultado contém o item.
- Adicionar o item `CAFE-500` com quantidade `2` e preço `18.90` e verificar total `37.80`.
- Adicionar mais de um item e verificar que o total é a soma de quantidade multiplicada pelo preço unitário de cada item.
- Verificar que o total muda de forma derivada dos itens, sem depender de um total informado separadamente.
- Adicionar item e verificar que o pedido original permanece vazio e `ABERTO`.
- Adicionar item e verificar que o novo pedido tem outro objeto de pedido e preserva o UUID conforme a decisão de identidade adotada.
- Alterar a coleção usada na criação ou adição depois da operação e verificar que o pedido não muda.
- Tentar modificar diretamente a coleção de itens exposta e verificar que a operação é recusada ou não altera o pedido.
- Pagar pedido `ABERTO` e verificar transição para `PAGO`.
- Cancelar pedido `ABERTO` e verificar transição para `CANCELADO`.
- Tentar pagar pedido `PAGO` e verificar erro.
- Tentar cancelar pedido `PAGO` e verificar erro.
- Tentar adicionar item a pedido `PAGO` e verificar erro.
- Tentar pagar pedido `CANCELADO` e verificar erro.
- Tentar cancelar pedido `CANCELADO` e verificar erro.
- Tentar adicionar item a pedido `CANCELADO` e verificar erro.
- Verificar que não existem dependências de Spring ou JPA no domínio.
- Verificar que as representações imutáveis usam `record` conforme definido, sem Lombok.
- Executar os testes usando o Maven Wrapper.
- Revisar o diff e confirmar que a mudança não altera `pom.xml`, infraestrutura ou código fora do escopo aprovado.

### Ambiguidades a decidir antes da implementação

- Ao adicionar um item, o novo pedido deve manter o mesmo UUID do pedido original ou receber um novo UUID? A regra de identidade precisa ser definida; os testes devem seguir essa decisão.
- O que significa “código do produto” vazio ou nulo? É necessário decidir se deve ser recusado e qual comportamento esperado.
- Devem ser permitidos itens repetidos para o mesmo produto ou a adição deve consolidar quantidade/preço?
- Qual escala e qual política de arredondamento devem ser usadas para `BigDecimal` quando o preço tiver mais casas decimais?
- Qual tipo de erro ou exceção representa quantidade/preço inválidos e transições recusadas? A spec define a recusa, mas não um contrato de exceção.
- “Usar records” se aplica a `Pedido` também, ou apenas a `ItemPedido`, `StatusPedido` e valores auxiliares? É preciso decidir considerando que o pedido precisa expor operações que devolvem novo estado.


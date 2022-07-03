# GraphQL Training

Este projeto foi criado para fins de testes e aprendizados da ferramenta GraphQL



### Query: teste

```graphql
{
    teste {
        subTeste {
            id
        }
    }
}
```
Descrição:
- Esta estrutura foi criada para validar se era necessário criar um resolver para o `type Teste` ou se poderia criar um resolver apenas para o `type subTeste`, visto que somente nele seria criado dados

### Query: user
```graphql
{
    user(id: "1") {
        name
    }
}
```
Descrição:
- Criado para testar como passar e receber argumentos no resolver

### Query: users
```graphql
{
    users {
        name,
        address {
            street
        }
    }
}
```
Descrição:
- Só pra ter uma query trazendo todos :)
- Mas também para validar o resolver do `Address`, pois uma coisa legal que entendi da engine é que, o objeto `User` **não tem** o objeto `Address` como dependência,
sendo assim, se o usuário não retornar o endereço, porém na query contiver o nó `address`, no resolver do `Address` conseguimos
pegar o `user` resultante do resolver anterior (nó user) e pegar o ID dele para buscar o endereço

### Query: openFinance
```graphql
{
    openFinance {
        id 
        extracts {
            balance 
            card {
                brand
            }
        }
    }
}
```
Descrição:
- Outro ponto interessante é ser possível passar por todas as diretivas e instrumentações com dados que vieram externamente.
Foi o caso desta query, onde simulo uma busca de dados antes de passá-lo para a engine do GraphQL via ContextGraphQL

### Directive: @Auth
```graphql
directive @Auth on FIELD_DEFINITION

type Extract {
    card: Card @Auth
    balance: Float
}
```
Descrição:
- Diretivas que são configuradas `on FIELD_DEFINITION`, são as diretivas que são declaradas na criação do `type`
- Este tipo de diretiva é implementada através do `SchemaDirectiveWiring`, neste caso, podemos alterar o comportamento do campo dado a uma diretiva
- Neste caso do `@Auth`, antes de executar o `fetcher` (resolver) do campo `card`, é feito uma validação de autenticação, caso o usuário não tenha permissão, o resolver não será executado

### Directive: @Required
```graphql
directive @Required on FIELD

{
    openFinance {
        id
        extracts @Required {
            balance
            card {
                brand
            }
        }
    }
}
```
Descrição:
- Diretivas `on FIELD` são diretivas que são declaradas no momento de realizar a `query`, ou seja, o cliente decidirá quando colocar
- Neste caso, o `@Required` é uma `instrumentação` e foi implementado pelo `SimpleInstrumentation`, com esta classe, conseguimos validar o ciclo de vida da execução da query, para o `@Required`, 
foi usado o momento em que a query é executada e validamos se o retorno é `null`, `vazio` em caso de String e `vazio` em caso de lista

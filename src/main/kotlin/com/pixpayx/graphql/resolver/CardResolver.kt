package com.pixpayx.graphql.resolver

import com.pixpayx.graphql.model.openfinance.Card
import com.pixpayx.graphql.model.openfinance.Extract
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class CardResolver : GraphQLResolver<Card> {
    override val type = "Extract"
    override val fieldName: String = "card"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> Card? = {
        val card = getSource<Extract>().card
        card.copy(brand = "${card.brand} - append suffix")
    }
}

/**
 * Resolver pra mostrar que é possível acessar os cards do open finance data via `getSource<Extract>`
 * pois o GraphQL executa um resolver "default" pra cada nó da query: `Property Data Fetcher`
 * Este data fetcher busca no "objeto" resultante do fetcher "pai" (type) uma propriedade com o nome definido em fieldName
 * Sendo assim, o "Extract" veio populado devido a já existir láaa no resolver do OpenFinanceData
 * e foi executado como `Property Data Fetcher`
 *
 * Por sua vez, neste resolver custom, o `Extract` existe no grafo,
 * e assim, o getSource consegue buscar o Extract e pegar o card dele
 * */
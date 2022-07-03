package com.pixpayx.graphql.resolver

import com.pixpayx.graphql.model.openfinance.OpenFinanceData
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class OpenFinanceDataResolver : GraphQLResolver<OpenFinanceData> {
    override val type = "Query"
    override val fieldName = "openFinance"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> OpenFinanceData? = {

        // This resolvers was created to get the OF Data and pass it through the GraphQL Engine
        // so it will be returned in Query Result and also will pass to all validations
        graphQlContext.get("openFinanceData") as? OpenFinanceData
    }
}

/**
 * Resolver de open finance, este foi recebido via context, em `fetcher` externo,
 * porém é possível passar por todas as validações criando um resolver pra ele
 * {
 *    openFinance {
 *       id
 *       extracts {
 *          balance
 *          card {
 *             brand
 *          }
 *       }
 *    }
 * }
 * */
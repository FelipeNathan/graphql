package com.pixpayx.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class TesteResolver : GraphQLResolver<Unit> {
    override val type = "Query"
    override val fieldName = "teste"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> Unit? = {
        null
    }
}

package com.pixpayx.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class SubTesteResolver : GraphQLResolver<SubTeste> {
    override val type = "Teste"
    override val fieldName = "subTeste"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> SubTeste? = {
        null
    }
}

data class SubTeste(val id: String)

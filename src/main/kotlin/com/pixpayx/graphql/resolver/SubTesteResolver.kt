package com.pixpayx.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class SubTesteResolver : GraphQLResolver<SubTeste> {
    override val type = "Teste"
    override val fieldName = "subTeste"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> SubTeste? = {
        SubTeste("TEEESTE")
    }
}

data class SubTeste(val id: String)

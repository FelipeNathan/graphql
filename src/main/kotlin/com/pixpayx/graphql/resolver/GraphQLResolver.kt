package com.pixpayx.graphql.resolver

import graphql.schema.DataFetchingEnvironment

interface GraphQLResolver<T> {
    val type: String
    val fieldName: String
    val fieldDataFetcher: DataFetchingEnvironment.() -> T?
}

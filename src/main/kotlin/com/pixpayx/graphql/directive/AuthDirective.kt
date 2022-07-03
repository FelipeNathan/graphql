package com.pixpayx.graphql.directive

import com.pixpayx.graphql.exception.AuthException
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import org.springframework.stereotype.Component

@Component
class AuthDirective : DirectiveResolver {

    override val directiveName = "Auth"

    override fun onField(env: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {

        val field = env.element
        val originalResolver = env.codeRegistry.getDataFetcher(env.fieldsContainer, field) as DataFetcher<*>
        val authResolver = AuthResolver(originalResolver)
        env.codeRegistry.dataFetcher(env.fieldsContainer, field, authResolver)
        return field
    }

    class AuthResolver(private val originalResolver: DataFetcher<*>) : DataFetcher<Any?> {

        override fun get(environment: DataFetchingEnvironment): Any? {
            val userRole = environment.graphQlContext.get("userRole") as String

            return if (userRole == "Admin") {
                originalResolver.get(environment)
            } else {
                throw AuthException()
            }
        }
    }
}
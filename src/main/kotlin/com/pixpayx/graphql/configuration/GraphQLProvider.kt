package com.pixpayx.graphql.configuration

import com.pixpayx.graphql.directive.DirectiveResolver
import com.pixpayx.graphql.resolver.GraphQLResolver
import graphql.GraphQL
import graphql.execution.instrumentation.ChainedInstrumentation
import graphql.execution.instrumentation.Instrumentation
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeRuntimeWiring
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class GraphQLProvider(
    val dataFetchers: List<GraphQLResolver<*>>,
    val directives: List<DirectiveResolver>,
    val instrumentations: List<Instrumentation>,
) {

    @Bean
    fun graphQL(): GraphQL {
        return this::class.java.getResourceAsStream("/schema.graphqls")!!
            .run {
                val typeDefinitionRegistry = SchemaParser().parse(this)

                val runtimeWiring = RuntimeWiring
                    .newRuntimeWiring()
                    .resolvers(dataFetchers)
                    .directives(directives)
                    .build()

                val schema = SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)

                GraphQL
                    .newGraphQL(schema)
                    .instrumentations(instrumentations)
                    .build()
            }
    }

    private fun RuntimeWiring.Builder.resolvers(dataFetchers: List<GraphQLResolver<*>>): RuntimeWiring.Builder {
        dataFetchers.forEach {
            TypeRuntimeWiring
                .newTypeWiring(it.type)
                .dataFetcher(it.fieldName, it.fieldDataFetcher)
                .run(this::type)
        }
        return this
    }

    private fun RuntimeWiring.Builder.directives(directives: List<DirectiveResolver>): RuntimeWiring.Builder {
        directives.forEach {
            directive(it.directiveName, it)
        }
        return this
    }

    private fun GraphQL.Builder.instrumentations(instrumentations: List<Instrumentation>): GraphQL.Builder {
        instrumentation(ChainedInstrumentation(instrumentations))
        return this
    }
}
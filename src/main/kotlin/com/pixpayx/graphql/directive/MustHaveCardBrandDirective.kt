package com.pixpayx.graphql.directive

import com.pixpayx.graphql.model.openfinance.Extract
import graphql.language.Argument
import graphql.language.StringValue
import graphql.schema.*
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import org.springframework.stereotype.Component
import java.lang.RuntimeException

// Existem formas de criar diretivas genéricas, que pudessem ser utilizadas em qualquer lugar como o usuário
// bem entendesse. Porém ficaria bem complexo pois:
//
// - não sabemos qual campo específico ele vai querer validar, portanto necessitaríamos colocar um novo ar-
//   gumento para que ele informasse esse campo. Aí a partir disso vem mais algumas complexidades:
//   - como precisamos colocar a diretiva no nó superior, o campo precisaria ser informado no formato
//     `card.brand`, por exemplo, adicionando mais complexidade para percorrer o grafo e encontrar os campos
//
// É possível contornar essas complexidades e criar algo genérico que fique legal, porém num primeiro momen-
// gostaria de discutir se vamos ter algum ganho implementando algo nesse sentido ou se é melhor ficarmos
// com a abordagem de implementar diretivas específicas pra cada caso de uso.

@Component
class MustHaveCardBrandDirective : DirectiveResolver {

    override val directiveName = DIRECTIVE_NAME

    override fun onField(env: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = env.element
        val originalResolver = env.codeRegistry.getDataFetcher(env.fieldsContainer, field) as DataFetcher<*>
        val authResolver = MustHaveCardBrandResolver(originalResolver)
        env.codeRegistry.dataFetcher(env.fieldsContainer, field, authResolver)
        return field
    }

    class MustHaveCardBrandResolver(private val originalResolver: DataFetcher<*>) : DataFetcher<Any?> {

        override fun get(environment: DataFetchingEnvironment): Any? {
            val data = originalResolver.get(environment)
            val field = environment.mergedField.singleField
            val directive = field.directives.find { it.name == DIRECTIVE_NAME }

            // A query não vai quebrar caso alguém coloque a diretiva onde não deveria, portanto cabe à nossa implemen-
            // tação validar se a diretiva está no campo correto. Talvez possamos até lançar uma exceção informando o
            // erro.
            if (field.name == "extracts" && directive != null) {
                val argument = directive.arguments.resolve("value")

                validate(data as List<Extract>, argument)
            }

            return data
        }

        private fun validate(data: List<Extract>, value: String) {
            val doesNotHaveExpectedValue = data.none { it.card.brand.contains(value, ignoreCase = true) }

            if (doesNotHaveExpectedValue) {
                throw MustHaveCardBrandException()
            }
        }
    }

    companion object {
        const val DIRECTIVE_NAME = "MustHaveCardBrand"
    }

}

private fun MutableList<Argument>.resolve(argumentName: String): String {
    val value = find { it.name == argumentName }!!.value
    return (value as StringValue).value!!
}

class MustHaveCardBrandException : RuntimeException(
    "Expected card brand not present among cards",
    null, false, false
)

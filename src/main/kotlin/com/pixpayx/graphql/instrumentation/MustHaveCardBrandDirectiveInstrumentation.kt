package com.pixpayx.graphql.instrumentation

import com.pixpayx.graphql.model.openfinance.Extract
import graphql.ErrorType
import graphql.GraphqlErrorBuilder
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import graphql.language.Field
import graphql.language.StringValue
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class MustHaveCardBrandDirectiveInstrumentation : SimpleInstrumentation() {

    private val InstrumentationFieldFetchParameters.singleField: Field
        get() = executionStepInfo.field.singleField

    override fun instrumentDataFetcher(dataFetcher: DataFetcher<*>, parameters: InstrumentationFieldFetchParameters): DataFetcher<*> {

        if (!parameters.singleField.hasDirective(DIRECTIVE_NAME)) {
            return super.instrumentDataFetcher(dataFetcher, parameters)
        }

        if (isDirectiveOnCorrectField(parameters)) {
            val cardDataFetcher = overrideDataFetcher(dataFetcher, parameters)
            return super.instrumentDataFetcher(cardDataFetcher, parameters)
        }

        return super.instrumentDataFetcher(dataFetcher, parameters)
    }

    private fun isDirectiveOnCorrectField(parameters: InstrumentationFieldFetchParameters): Boolean {
        if (parameters.singleField.name == "extracts") {
            return true
        }

        val error = GraphqlErrorBuilder
            .newError()
            .errorType(ErrorType.ValidationError)
            .message("Directive is not for field ${parameters.singleField.name}")
            .build()

        parameters.executionContext.addError(error)
        return false
    }

    private fun overrideDataFetcher(dataFetcher: DataFetcher<*>, parameters: InstrumentationFieldFetchParameters): DataFetcher<Any?> {
        val requiredBrand = parameters
            .singleField
            .getDirectives(DIRECTIVE_NAME)
            .first()
            .getArgument(DIRECTIVE_ARGUMENT)
            .value.run { this as StringValue }
            .value

        return DataFetcher { env ->
            val data = dataFetcher.extracts(env)
            if (!data.containsBrand(requiredBrand)) {
                val error = GraphqlErrorBuilder
                    .newError()
                    .errorType(ErrorType.ValidationError)
                    .message("Extract must have a card of $requiredBrand")
                    .build()

                parameters.executionContext.addError(error)
                null
            } else data
        }
    }

    private fun List<Extract>.containsBrand(requiredBrand: String) = any { it.card.brand.contains(requiredBrand) }

    private fun DataFetcher<*>.extracts(env: DataFetchingEnvironment): List<Extract> = this.get(env) as List<Extract>

    companion object {
        const val DIRECTIVE_NAME = "RequiredCardBrand"
        const val DIRECTIVE_ARGUMENT = "brand"
    }
}

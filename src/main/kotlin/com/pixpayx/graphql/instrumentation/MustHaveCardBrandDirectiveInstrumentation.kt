package com.pixpayx.graphql.instrumentation

import com.pixpayx.graphql.model.openfinance.Extract
import graphql.ErrorType
import graphql.ExecutionResult
import graphql.GraphqlErrorBuilder
import graphql.execution.FetchedValue
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationFieldCompleteParameters
import graphql.language.Field
import graphql.language.StringValue
import org.springframework.stereotype.Component

@Component
class MustHaveCardBrandDirectiveInstrumentation : SimpleInstrumentation() {

    override fun beginFieldComplete(parameters: InstrumentationFieldCompleteParameters): InstrumentationContext<ExecutionResult> {

        if (!parameters.singleField.hasDirective("MustHaveCardBrand")) {
            return super.beginFieldComplete(parameters)
        }

        if (isDirectiveOnCorrectField(parameters)) {
            addErrorIfDoNotContainCard(parameters)
        }

        return super.beginFieldComplete(parameters)
    }

    private fun isDirectiveOnCorrectField(parameters: InstrumentationFieldCompleteParameters): Boolean {
        if (parameters.singleField.name == "extracts") {
            return true
        }

        val error = GraphqlErrorBuilder
            .newError()
            .errorType(ErrorType.ValidationError)
            .message("Directive for wrong field")
            .build()

        parameters.executionContext.addError(error)
        return false
    }

    private fun addErrorIfDoNotContainCard(parameters: InstrumentationFieldCompleteParameters) {
        val requiredCard = parameters
            .singleField
            .getDirectives("MustHaveCardBrand")
            .first()
            .getArgument("brand")
            .value.run { this as StringValue }
            .value


        if (!parameters.extracts.any { it.card.brand.contains(requiredCard) }) {
            val error = GraphqlErrorBuilder
                .newError()
                .errorType(ErrorType.ValidationError)
                .message("Extract must have a card of $requiredCard")
                .build()

            parameters.executionContext.addError(error)
            return
        }
    }

    private val InstrumentationFieldCompleteParameters.extracts: List<Extract>
        get() = ((fetchedValue as? FetchedValue)?.fetchedValue as? List<Extract>).orEmpty()

    private val InstrumentationFieldCompleteParameters.singleField: Field
        get() = executionStepInfo.field.singleField

    private val InstrumentationFieldCompleteParameters.fieldPath: String
        get() = executionStepInfo.path.toString()
}

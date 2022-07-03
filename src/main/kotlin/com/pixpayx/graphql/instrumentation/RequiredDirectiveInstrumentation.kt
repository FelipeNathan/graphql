package com.pixpayx.graphql.instrumentation

import graphql.ErrorType
import graphql.ExecutionResult
import graphql.GraphqlErrorBuilder
import graphql.execution.FetchedValue
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationFieldCompleteParameters
import graphql.language.Field
import org.springframework.stereotype.Component

@Component
class RequiredDirectiveInstrumentation : SimpleInstrumentation() {

    override fun beginFieldComplete(parameters: InstrumentationFieldCompleteParameters): InstrumentationContext<ExecutionResult> {

        if (parameters.singleField.hasDirective("Required")) {
            addErrorIfNull(parameters)
        }

        return super.beginFieldComplete(parameters)
    }

    private fun addErrorIfNull(parameters: InstrumentationFieldCompleteParameters) {

        if (!parameters.parsedFetchedValue.isNull()) {
            return
        }

        val fieldName = parameters.singleField.name
        val error = GraphqlErrorBuilder
            .newError()
            .errorType(ErrorType.DataFetchingException)
            .message("Field $fieldName is marked as required")
            .build()

        parameters.executionContext.addError(error)
    }

    private fun FetchedValue?.isNull(): Boolean {
        return when (val value = this?.fetchedValue) {
            is String -> value.isBlank()
            is Collection<*> -> value.isEmpty()
            else -> value == null
        }
    }
}

val InstrumentationFieldCompleteParameters.parsedFetchedValue: FetchedValue?
    get() = fetchedValue as? FetchedValue

val InstrumentationFieldCompleteParameters.singleField: Field
    get() = executionStepInfo.field.singleField
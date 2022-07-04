package com.pixpayx.graphql.instrumentation

import com.pixpayx.graphql.exception.error.RequiredFieldError
import graphql.ExecutionResult
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

        if (!parameters.asFetchedValue.isNull()) {
            return
        }

        parameters.executionContext.addError(RequiredFieldError(parameters.fieldPath))
    }

    private fun FetchedValue?.isNull(): Boolean {
        return when (val value = this?.fetchedValue) {
            is String -> value.isBlank()
            is Collection<*> -> value.isEmpty()
            else -> value == null
        }
    }

    private val InstrumentationFieldCompleteParameters.asFetchedValue: FetchedValue?
        get() = fetchedValue as? FetchedValue

    private val InstrumentationFieldCompleteParameters.singleField: Field
        get() = executionStepInfo.field.singleField

    private val InstrumentationFieldCompleteParameters.fieldPath: String
        get() = executionStepInfo.path.toString()
}

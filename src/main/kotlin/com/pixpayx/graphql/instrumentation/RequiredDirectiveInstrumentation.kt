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

        if (parameters.singleField.hasDirective(DIRECTIVE_NAME)) {
            addErrorIfNull(parameters)
        }

        // A `Instrumentacão` executa em TODOS os nós, independente se tem ou não a configuração na query
        // Desta forma, poderíamos validar se este nó contém filhos com required
        // (ignore os "netos", pois chegará a vez deles ainda)
        if (parameters.asFetchedValue.isNull()) {
            checkIfHaveRequiredChildrens(parameters)
        }

        return super.beginFieldComplete(parameters)
    }

    private fun checkIfHaveRequiredChildrens(parameters: InstrumentationFieldCompleteParameters) {
        parameters.singleField.selectionSet.selections.forEach {
            if (it is Field && it.hasDirective(DIRECTIVE_NAME)) {
                parameters.executionContext.addError(RequiredFieldError(parameters.fieldPath))
            }
        }
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

    companion object {
        const val DIRECTIVE_NAME = "Required"
    }
}

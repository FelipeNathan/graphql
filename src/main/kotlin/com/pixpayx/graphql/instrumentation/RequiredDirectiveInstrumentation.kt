package com.pixpayx.graphql.instrumentation

import com.pixpayx.graphql.exception.error.RequiredFieldError
import graphql.ExecutionResult
import graphql.execution.FetchedValue
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationFieldCompleteParameters
import graphql.language.DirectivesContainer
import graphql.language.Field
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.language.NamedNode
import graphql.language.Selection
import graphql.language.SelectionSetContainer
import org.springframework.stereotype.Component

@Component
class RequiredDirectiveInstrumentation : SimpleInstrumentation() {

    override fun beginFieldComplete(parameters: InstrumentationFieldCompleteParameters): InstrumentationContext<ExecutionResult> {

        if (parameters.singleField.hasDirective(DIRECTIVE_NAME)) {
            addErrorIfNull(parameters)
        } else {
            // A `Instrumentacão` executa em TODOS os nós, independente se tem ou não a configuração na query
            // Desta forma, poderíamos validar se este nó contém filhos com required
            if (parameters.asFetchedValue.isNull()) {
                validateChildrenFields(parameters)
            }
        }

        return super.beginFieldComplete(parameters)
    }

    private fun addErrorIfNull(parameters: InstrumentationFieldCompleteParameters) {
        if (parameters.asFetchedValue.isNull()) {
            parameters.executionContext.addError(RequiredFieldError(parameters.fieldPath))
        }
    }

    private fun FetchedValue?.isNull(): Boolean {
        return when (val value = this?.fetchedValue) {
            is String -> value.isBlank()
            is Collection<*> -> value.isEmpty()
            else -> value == null
        }
    }

    /*
    * Busca os campos filhos do nó atual
    */
    private fun validateChildrenFields(parameters: InstrumentationFieldCompleteParameters) {
        parameters.queryFields.forEach {
            addErrorIfFieldHaveRequiredDirective(it, parameters)
        }
    }

    /*
    * Os campos Selection, podem ser: Field, InlineFragment e FragmentSpread
    * Field é o campo propriamente dito e este pode ser validado se existe ou não uma diretiva
    * Fragment é um meio de "agrupar" atributos para serem reutilzados em várias queries
    */
    private fun addErrorIfFieldHaveRequiredDirective(
        field: Selection<*>,
        parameters: InstrumentationFieldCompleteParameters,
        parent: Selection<*>? = null,
    ) {
        if (field.hasRequiredDirective()) {
            val path = getFullPath(field.fieldName, parent?.fieldName, parameters.fieldPath)
            parameters.executionContext.addError(RequiredFieldError(path))
            return
        }

        /*
        * Quando o campo é um fragment (previamente criado), deve-se buscar o FragmentDefinition
        * (definição do fragmento), pois não está "dentro da query" os atributos.
        * Diferente de um "InlineFragment" que é definido o fragmento dentro da query
        */
        if (field is FragmentSpread) {
            parameters.executionContext.fragmentsByName[field.name]?.selectionSet?.selections?.forEach {
                addErrorIfFieldHaveRequiredDirective(it, parameters, field)
            }
        } else {
            val container = (field as SelectionSetContainer<*>)
            container.selectionSet?.selections?.forEach {
                addErrorIfFieldHaveRequiredDirective(it, parameters, field)
            }
        }
    }

    /*
    * Os campos Selection, podem ser: Field, InlineFragment e FragmentSpread
    * Os tipos Field e FragmentSpread herdam de NamedNode, onde conseguimos pegar o nome do campo
    * Já o InlineFragment, só conseguimos pegar o nome pelo atributo "typeCondition" que é do tipo TypeName
    * */
    private val Selection<*>.fieldName: String
        get() = when (this) {
            is InlineFragment -> "${typeCondition.name} [fragment]"
            else -> (this as NamedNode<*>).name
        }

    /*
    * Todos os selections são directives container também
    * */
    private fun Selection<*>.hasRequiredDirective() = (this as DirectivesContainer<*>).hasDirective(DIRECTIVE_NAME)

    private fun getFullPath(fieldPath: String, parentPath: String?, rootPath: String): String {
        val path = parentPath?.run {
            when {
                endsWith("/") -> this
                else -> "$this/"
            }
        }
        return "${rootPath}/$path${fieldPath}"
    }

    private val InstrumentationFieldCompleteParameters.asFetchedValue: FetchedValue?
        get() = fetchedValue as? FetchedValue

    private val InstrumentationFieldCompleteParameters.singleField: Field
        get() = executionStepInfo.field.singleField

    private val InstrumentationFieldCompleteParameters.fieldPath: String
        get() = executionStepInfo.path.toString()

    private val InstrumentationFieldCompleteParameters.queryFields: List<Selection<*>>
        get() = executionStepInfo.field.singleField.selectionSet.selections

    companion object {
        const val DIRECTIVE_NAME = "Required"
    }
}

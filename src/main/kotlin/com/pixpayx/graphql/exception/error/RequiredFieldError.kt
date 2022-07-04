package com.pixpayx.graphql.exception.error

import graphql.ErrorClassification
import graphql.GraphQLError
import graphql.language.SourceLocation

class RequiredFieldError(private val fieldPath: String) : GraphQLError {

    override fun getMessage(): String {
        return "Field $fieldPath is marked as required and got empty or null"
    }

    override fun getLocations(): MutableList<SourceLocation>? {
        return null
    }

    override fun getErrorType(): ErrorClassification {
        return OurErrorType.RequiredFieldError
    }
}

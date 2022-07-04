package com.pixpayx.graphql.exception.error

import graphql.ErrorClassification

enum class OurErrorType : ErrorClassification {
    RequiredFieldError
}
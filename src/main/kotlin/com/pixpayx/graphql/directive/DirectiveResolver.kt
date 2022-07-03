package com.pixpayx.graphql.directive

import graphql.schema.idl.SchemaDirectiveWiring

interface DirectiveResolver : SchemaDirectiveWiring {
    val directiveName: String
}
package com.pixpayx.graphql.controller

import com.pixpayx.graphql.model.openfinance.OpenFinanceData
import com.pixpayx.graphql.repository.ObkRepository
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class GraphQLController(
    val graphQL: GraphQL,
    val obkRepository: ObkRepository
) {

    @PostMapping("/graphql", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun graphql(@RequestBody graphQLRequest: GraphQLRequest): ExecutionResult {

        //Collect userId by Identity
        val userId = graphQLRequest.variables["userId"].toString()

        //Fetch OF Data by interactor
        val openFinanceData = obkRepository.fetchOpenFinanceData().firstOrNull { it.id == userId }

        //Create a context and pass the OF Data to it, it'll be used inside the resolvers to bring it to query result
        //And validations (directives, instrumentations, etc)
        val context = Context(
            userId = userId.toLong(),
            userType = graphQLRequest.variables["userType"].toString(),
            userRole = graphQLRequest.variables["userRole"].toString(),
            openFinanceData = openFinanceData
        )

        //Execute the query in GraphQL Engine
        val executionInput = ExecutionInput.newExecutionInput(graphQLRequest.query)
            .variables(graphQLRequest.variables)
            .graphQLContext(context.toMap())

        return graphQL.execute(executionInput)
    }
}

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any> = emptyMap(),
    val operationName: String?,
)

data class Context(
    val userId: Long,
    val userType: String,
    val userRole: String,
    val openFinanceData: OpenFinanceData?
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "userType" to userType,
            "userRole" to userRole,
            "openFinanceData" to openFinanceData
        )
    }
}
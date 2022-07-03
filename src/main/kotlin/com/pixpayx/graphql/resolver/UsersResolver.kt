package com.pixpayx.graphql.resolver

import com.pixpayx.graphql.model.customtests.User
import com.pixpayx.graphql.repository.UserRepository
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class UsersResolver(
    val userRepository: UserRepository
) : GraphQLResolver<List<User>> {
    override val type = "Query"
    override val fieldName = "users"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> List<User>? = {
        userRepository.getAll()
    }
}

/**
 * {
 *    Resolver do `all` users, pra quando for executada a query users
 *    user(id: "1") {
 *      id
 *    }
 * }
 * */
package com.pixpayx.graphql.resolver

import com.pixpayx.graphql.model.customtests.User
import com.pixpayx.graphql.repository.UserRepository
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class UserResolver(
    val userRepository: UserRepository
) : GraphQLResolver<User?> {
    override val type = "Query"
    override val fieldName = "user"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> User? = {
        userRepository.findById(arguments["id"].toString().toLong())
    }
}

/**
 * {
 *    Resolver do `single` User, pra quando for informado o id do usuário na query user
 *    user(id: "1") {
 *      name
 *    }
 * }
 * */
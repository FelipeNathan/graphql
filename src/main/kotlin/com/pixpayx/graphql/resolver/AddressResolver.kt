package com.pixpayx.graphql.resolver

import com.pixpayx.graphql.model.customtests.Address
import com.pixpayx.graphql.model.customtests.User
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class AddressResolver : GraphQLResolver<Address> {
    override val type = "User"
    override val fieldName = "address"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> Address? = {
        addresses.firstOrNull { it.userId == getSource<User>().id }
    }
}

// Some repository result (findAddressByUser)
val addresses = listOf(
    Address("Bora cumê pinhão", 1L),
    Address("Mas em curitiba é mais frio", 2L)
)

/**
 * {
 *    Resolver do address, pra quando for informado o address na query
 *    user {
 *      address { street }
 *    }
 * }
 * */
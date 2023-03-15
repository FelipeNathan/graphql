package com.pixpayx.graphql.resolver

import com.pixpayx.graphql.model.customtests.Address
import com.pixpayx.graphql.model.customtests.User
import com.pixpayx.graphql.repository.AddressRepository
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Component
class AddressResolver(
    val addressRepository: AddressRepository
) : GraphQLResolver<Address> {
    override val type = "User"
    override val fieldName = "address"
    override val fieldDataFetcher: DataFetchingEnvironment.() -> Address? = {
        addressRepository.findByUser(getSource<User>().id)
    }
}


/**
 * {
 *    Resolver do address, pra quando for informado o address na query
 *    user {
 *      address { street }
 *    }
 * }
 * */
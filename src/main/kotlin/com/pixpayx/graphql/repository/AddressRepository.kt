package com.pixpayx.graphql.repository

import com.pixpayx.graphql.model.customtests.Address
import org.springframework.stereotype.Repository

@Repository
class AddressRepository {
    fun findByUser(userId: Long) = addresses.firstOrNull { it.userId == userId }
}

// Some repository result (findAddressByUser)
val addresses = listOf(
    Address("Bora cumê pinhão", 1L),
    Address("Mas em curitiba é mais frio", 2L)
)
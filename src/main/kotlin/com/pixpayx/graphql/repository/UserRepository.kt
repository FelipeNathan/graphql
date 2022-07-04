package com.pixpayx.graphql.repository

import com.pixpayx.graphql.model.customtests.User
import org.springframework.stereotype.Repository

@Repository
class UserRepository {
    fun getAll() = users
    fun findById(id: Long): User? = users.firstOrNull { it.id == id }
}

val users = listOf(
    User(1L, "CONSUMER", "Felipe"),
    User(2L, "SELLER", "Xuão"),
    User(3L, "OUTRO TIPO", "Baby Shark") // User without address
)
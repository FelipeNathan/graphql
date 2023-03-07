package com.pixpayx.graphql.repository

import com.pixpayx.graphql.model.customtests.CreditAccount
import com.pixpayx.graphql.model.customtests.PrePaidAccount
import com.pixpayx.graphql.model.customtests.User
import org.springframework.stereotype.Repository

@Repository
class UserRepository {
    fun getAll() = users
    fun findById(id: Long): User? = users.firstOrNull { it.id == id }
}

val accounts = listOf(
    CreditAccount("visa", "tchaca-laca-bum"),
    PrePaidAccount(true, "Aqui é só no débito garaio")
)

val users = listOf(
    User(1L, "CONSUMER", "Felipe", accounts),
    User(2L, "SELLER", "Xuão", accounts),
    User(3L, "OUTRO TIPO", "Baby Shark") // User without address & accounts
)

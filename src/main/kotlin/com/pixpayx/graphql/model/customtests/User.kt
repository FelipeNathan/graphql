package com.pixpayx.graphql.model.customtests

data class User(
    val id: Long,
    val type: String,
    val name: String,
    val accounts: List<Account>? = null
)
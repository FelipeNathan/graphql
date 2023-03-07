package com.pixpayx.graphql.model.customtests

interface Account {
    val name: String
}

data class PrePaidAccount(
    val useCheck: Boolean,
    override val name: String
) : Account

data class CreditAccount(
    val brand: String,
    override val name: String
) : Account
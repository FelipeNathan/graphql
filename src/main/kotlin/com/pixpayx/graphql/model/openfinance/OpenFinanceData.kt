package com.pixpayx.graphql.model.openfinance

data class OpenFinanceData(val id: String, val extracts: List<Extract>? = null)
data class Extract(val balance: Float, val card: Card)
data class Card(val brand: String)
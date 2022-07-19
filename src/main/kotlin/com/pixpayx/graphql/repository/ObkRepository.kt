package com.pixpayx.graphql.repository

import com.pixpayx.graphql.model.openfinance.Card
import com.pixpayx.graphql.model.openfinance.Extract
import com.pixpayx.graphql.model.openfinance.OpenFinanceData
import org.springframework.stereotype.Repository

@Repository
class ObkRepository {
    fun fetchOpenFinanceData(): List<OpenFinanceData> = ofs
}

val ofs = listOf(
    OpenFinanceData(
        id = "1",
    ),

    OpenFinanceData(
        id = "2",
        extracts = listOf(
            Extract(
                balance = 10.0f,
                card = Card("PicPay")
            ),
            Extract(
                balance = 567.0f,
                card = Card("NuBank")
            )
        )
    )
)
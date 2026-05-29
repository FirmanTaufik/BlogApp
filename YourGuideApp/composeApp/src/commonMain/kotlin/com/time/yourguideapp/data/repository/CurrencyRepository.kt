package com.time.yourguideapp.data.repository

import com.time.yourguideapp.presentation.currency.CurrencyRate
import com.time.yourguideapp.presentation.currency.CurrencyRatesSnapshot
import com.time.yourguideapp.presentation.currency.CurrencyTarget
import com.time.yourguideapp.presentation.currency.usdTargets
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CurrencyRepository(
    private val client: HttpClient,
) {
    suspend fun loadUsdExchangeRates(): CurrencyRatesSnapshot {
        val quotes = usdTargets.joinToString(",") { it.code }
        val response = client.get(
            "https://api.frankfurter.dev/v2/rates?base=USD&quotes=$quotes",
        ).body<String>()

        val ratesByCode = Regex(
            """"quote"\s*:\s*"([A-Z]{3})"[\s\S]*?"rate"\s*:\s*([0-9.]+)""",
        ).findAll(response)
            .mapNotNull { match ->
                val code = match.groupValues.getOrNull(1).orEmpty()
                val value = match.groupValues.getOrNull(2).orEmpty().toDoubleOrNull()
                val target = usdTargets.firstOrNull { it.code == code }
                if (target != null && value != null) {
                    CurrencyRate(target = target, value = value)
                } else {
                    null
                }
            }
            .sortedBy { rate -> usdTargets.indexOf(rate.target) }
            .toList()

        return CurrencyRatesSnapshot(
            date = response.findFirstDate().orEmpty(),
            rates = ratesByCode,
        )
    }
}

private fun String.findFirstDate(): String? {
    return Regex(""""date"\s*:\s*"([^"]+)"""")
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
}

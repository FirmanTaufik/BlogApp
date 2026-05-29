package com.time.yourguideapp.presentation.currency

data class CurrencyTarget(
    val code: String,
    val name: String,
    val flagCode: String,
)

data class CurrencyRate(
    val target: CurrencyTarget,
    val value: Double,
)

data class CurrencyRatesSnapshot(
    val date: String,
    val rates: List<CurrencyRate>,
)

data class CurrencyUiState(
    val rates: List<CurrencyRate> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val updatedDate: String? = null,
)

val usdTargets = listOf(
    CurrencyTarget("EUR", "Euro", "eu"),
    CurrencyTarget("GBP", "British Pound", "gb"),
    CurrencyTarget("JPY", "Japanese Yen", "jp"),
    CurrencyTarget("CNY", "Chinese Yuan", "cn"),
    CurrencyTarget("SGD", "Singapore Dollar", "sg"),
    CurrencyTarget("IDR", "Indonesian Rupiah", "id"),
    CurrencyTarget("MYR", "Malaysian Ringgit", "my"),
    CurrencyTarget("THB", "Thai Baht", "th"),
    CurrencyTarget("VND", "Vietnamese Dong", "vn"),
    CurrencyTarget("PHP", "Philippine Peso", "ph"),
    CurrencyTarget("KRW", "South Korean Won", "kr"),
    CurrencyTarget("HKD", "Hong Kong Dollar", "hk"),
    CurrencyTarget("AUD", "Australian Dollar", "au"),
    CurrencyTarget("NZD", "New Zealand Dollar", "nz"),
    CurrencyTarget("CAD", "Canadian Dollar", "ca"),
    CurrencyTarget("CHF", "Swiss Franc", "ch"),
    CurrencyTarget("AED", "UAE Dirham", "ae"),
    CurrencyTarget("SAR", "Saudi Riyal", "sa"),
    CurrencyTarget("TRY", "Turkish Lira", "tr"),
    CurrencyTarget("BRL", "Brazilian Real", "br"),
    CurrencyTarget("MXN", "Mexican Peso", "mx"),
    CurrencyTarget("INR", "Indian Rupee", "in"),
)

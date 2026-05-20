package com.time.yourguideapp.auth

object GoogleSignInConfig {
    const val WEB_CLIENT_ID: String = "752154779144-ief6f9j0plu3dk58jq4nmse8to6ndpdp.apps.googleusercontent.com"

    val isConfigured: Boolean
        get() = WEB_CLIENT_ID.isNotBlank()
}

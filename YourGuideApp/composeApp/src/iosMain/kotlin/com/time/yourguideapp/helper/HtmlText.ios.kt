package com.time.yourguideapp.helper

actual fun htmlToPlainText(html: String): String {
    return html.replace(Regex("<[^>]*>"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}


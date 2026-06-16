package com.time.yourguideapp.helper

import org.jsoup.Jsoup

actual fun htmlToPlainText(html: String): String {
    return Jsoup.parse(html).text()
}


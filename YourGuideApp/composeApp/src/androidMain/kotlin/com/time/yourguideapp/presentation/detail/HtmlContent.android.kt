package com.time.yourguideapp.presentation.detail

import android.graphics.Color
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun HtmlContent(
    html: String,
    modifier: Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                settings.javaScriptEnabled = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
        },
        update = { webView ->
            if (webView.tag != html) {
                webView.tag = html
                webView.loadDataWithBaseURL(
                    null,
                    html.withResponsiveViewport(),
                    "text/html",
                    "UTF-8",
                    null,
                )
            }
        },
    )
}

private fun String.withResponsiveViewport(): String {
    return """
        <!DOCTYPE html>
        <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        padding: 0;
                        color: #123060;
                        font-size: 13px;
                        line-height: 1.45;
                        font-family: sans-serif;
                        background: transparent;
                    }
                    img, iframe, video {
                        max-width: 100%;
                        height: auto;
                    }
                </style>
            </head>
            <body>
                $this
            </body>
        </html>
    """.trimIndent()
}

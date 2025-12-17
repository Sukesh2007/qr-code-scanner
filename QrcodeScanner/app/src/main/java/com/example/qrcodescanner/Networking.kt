package com.example.qrcodescanner

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object Networking{
    private val client: HttpClient = HttpClient(OkHttp){
        install(ContentNegotiation){
            json(
                contentType = ContentType("text", "plain")
            )
        }
    }

    fun checkHtml(text: String) : Boolean{
        if(text.isNotBlank()){
            val normalText: Boolean = text.trim().startsWith(prefix = "http://", ignoreCase = false)
            val httpsText: Boolean = text.trim().startsWith("https://", ignoreCase = false)
            if(normalText || httpsText) return true
        }
        return false
    }

    suspend fun clientCall(link: String, context: Context): Any {
        val response = client.get(link)

        val contentType = response.contentType()?.withoutParameters()

        return when (contentType) {
            ContentType.Text.Plain -> {
                response.bodyAsText()
            }

            ContentType.Application.Json -> {
                response.body<Map<String, Any>>()
            }

            ContentType.Text.Html -> {
                val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                withContext(Dispatchers.Main) {
                    context.startActivity(intent)
                }
                "Opened in browser"
            }

            else -> {
                "Unsupported Content-Type: $contentType"
            }
        }
    }

}


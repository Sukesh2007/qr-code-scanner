package com.example.qrcodescanner

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


object Networking{
    private val client: HttpClient = HttpClient(OkHttp){
        install(ContentNegotiation){
            json()
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

        try {
            return when (contentType) {
                ContentType.Text.Plain -> {
                    response.bodyAsText()
                }

                ContentType.Application.Json -> {
                    val give = response.bodyAsText()
                    val formatted = Json {
                        prettyPrint = true
                    }.encodeToString(
                        JsonElement.serializer(),
                        Json.parseToJsonElement(give)
                    )
                    formatted
                }

                ContentType.Text.Html -> {
                    val intent = Intent(Intent.ACTION_VIEW, link.toUri())
                    withContext(Dispatchers.Main) {
                        context.startActivity(intent)
                    }
                    link
                }

                else -> {
                    "Unsupported Content-Type: $contentType"
                }
            }
        }catch(e: Exception){
            e.printStackTrace()
            Log.d("Networking", e.message.toString())
            println("The mistake is in Networking.kt file")
            return "Error in Networking.kt"
        }
    }

}


package com.example.myapplication.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Request data class representing the input JSON structure
data class GroqRequest(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

// Response data class representing the output JSON structure
data class GroqResponse(
    val id: String,
    val objectType: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val queue_time: Float,
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

// Groq API Interface definition
interface GroqApi {
    @Headers("Authorization: Bearer Your API key")  // Make sure to replace YOUR_API_KEY with the actual API key
    @POST("openai/v1/chat/completions")
    suspend fun sendMessage(@Body request: GroqRequest): GroqResponse
}

object GroqApiService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.groq.com/")  // Use the base URL for Groq API
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(GroqApi::class.java)

    suspend fun getResponse(input: String): String? {
        return try {
            val messages = listOf(Message("user", input))
            val request = GroqRequest(model = "llama-3.3-70b-versatile", messages = messages)
            val response = api.sendMessage(request)
            // Get the content from the first choice in the response
            response.choices.firstOrNull()?.message?.content
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

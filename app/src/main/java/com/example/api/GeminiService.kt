package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Moshi Representation of Gemini generateContent payload
data class Part(
    val text: String? = null
)

data class Content(
    val parts: List<Part>
)

data class Tool(
    val googleSearch: Map<String, String>? = null
)

data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null,
    val tools: List<Tool>? = null
)

data class WebSource(
    val uri: String? = null,
    val title: String? = null
)

data class GroundingChunk(
    val web: WebSource? = null
)

data class GroundingMetadata(
    val webSearchQueries: List<String>? = null,
    val groundingChunks: List<GroundingChunk>? = null
)

data class Candidate(
    val content: Content,
    val groundingMetadata: GroundingMetadata? = null
)

data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

// Structured Prediction Result containing Google Search facts
data class PredictionResult(
    val predictionText: String,
    val searchQueries: List<String> = emptyList(),
    val searchSources: List<Pair<String, String>> = emptyList()
)

object GeminiPredictor {
    suspend fun analyzeMatch(
        teamA: String,
        teamB: String,
        history: String,
        formTeamA: String,
        formTeamB: String,
        language: String
    ): PredictionResult {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            val missingText = if (language == "VI") {
                "Thiếu khóa API Gemini. Vui lòng thiết lập GEMINI_API_KEY trong phần quản lý Secrets."
            } else {
                "Gemini API Key is missing. Please configure GEMINI_API_KEY in the AI Studio Secrets panel."
            }
            return PredictionResult(predictionText = missingText)
        }

        val systemInstruction = "You are WC-Predictor-AI, a world-class football tactical computer for World Cup 2026. Provide strategic predictions, tactical boards analysis and key match-ups details."
        
        val prompt = if (language == "VI") {
            """
            Phân tích trận đấu vòng bảng World Cup 2026: $teamA đối đầu $teamB.
            Lịch sử đối đầu gần đây (qua công cụ tìm kiếm): $history
            Phong độ gần đây của $teamA: $formTeamA
            Phong độ gần đây của $teamB: $formTeamB
            
            Hãy sử dụng Google Search để tìm kiếm và cập nhật dữ liệu THẬT, tin tức mới nhất về lực lượng, chấn thương và phong độ của hai đội tuyển này tính đến thời điểm hiện tại.
            
            Vui lòng cung cấp phân tích bằng Tiếng Việt với bố cục chuyên nghiệp:
            1. 📊 PHÂN TÍCH CHIẾN THUẬT (Sơ đồ chiến thuật đề xuất, điểm mạnh/yếu của mỗi đội)
            2. 🎯 DỰ ĐOÁN TỶ SỐ (Tỷ số dự kiến cụ thể, xác suất thắng/hòa/thua)
            3. ⭐ CẦU THỦ TOẢ SÁNG (Nhân tố quyết định trận đấu)
            4. 💡 LỜI KHUYÊN PHANS (Gợi ý diễn biến tâm điểm)
            """.trimIndent()
        } else {
            """
            Analyze the World Cup 2026 Group Stage Match: $teamA vs $teamB.
            Head-to-Head History: $history
            Recent Form of $teamA: $formTeamA
            Recent Form of $teamB: $formTeamB
            
            Use Google Search to fetch to update real data, live updates, and team injury or form changes regarding these squads.
            
            Please provide a professional analytical prediction in English with sections:
            1. 📊 TACTICAL ANALYSIS (Suggested lineups, strengths/weaknesses)
            2. 🎯 PROJECTED SCORELINE (Exact predicted score, percentage probabilities)
            3. ⭐ KEY PLAYERS (Match-winning individuals)
            4. 💡 GEEK STAT (Key trend or focal points to watch)
            """.trimIndent()
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction))),
            tools = listOf(Tool(googleSearch = emptyMap())) // Grounding via Google Search enabled!
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val candidate = response.candidates?.firstOrNull()
            val textValue = candidate?.content?.parts?.firstOrNull()?.text 
                ?: (if (language == "VI") "Không thể phản hồi tự động." else "Empty response from AI.")
            
            // Extract Search Grounding metadata
            val queries = candidate?.groundingMetadata?.webSearchQueries ?: emptyList()
            val sources = mutableListOf<Pair<String, String>>()
            candidate?.groundingMetadata?.groundingChunks?.forEach { chunk ->
                val web = chunk.web
                if (web != null && !web.uri.isNullOrEmpty()) {
                    val title = web.title ?: "Google Source"
                    sources.add(Pair(title, web.uri))
                }
            }

            PredictionResult(
                predictionText = textValue,
                searchQueries = queries,
                searchSources = sources
            )
        } catch (e: Exception) {
            val errText = if (language == "VI") {
                "Lỗi phân tích AI: ${e.localizedMessage ?: e.message}"
            } else {
                "AI analysis error: ${e.localizedMessage ?: e.message}"
            }
            PredictionResult(
                predictionText = errText,
                searchQueries = emptyList(),
                searchSources = emptyList()
            )
        }
    }
}

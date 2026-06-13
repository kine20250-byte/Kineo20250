package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- نماذج البيانات الخاصة بمنصة جوجل جيميني باستخدام موشي ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

// --- واجهة ريتروفيت لاستدعاء خادم جيميني ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

// --- مشغل شبكة ريتروفيت للاتصال بالذكاء الاصطناعي ---

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

/**
 * عميل الذكاء الاصطناعي القانوني لاستدعاء جيميني مجاناً والتحقق وإعداد التقارير
 */
object GeminiLegalClient {

    private const val SYSTEM_ROLE = """
        أنت "المستشار القضائي اليمني الذكي (AGIX)"، خبير دستوري وقانوني وأحد كبار قضاة اليمن. 
        مهمتك مراجعة القضايا، فحص التهم، وتدقيق ملفات السجناء والمدعين.
        يجب أن تستشهد بدقة بالقوانين والتشريعات اليمنية ذات الصلة، خاصة:
        - دستور الجمهورية اليمنية
        - قانون الإجراءات الجزائية اليمني (مثل الحبس الاحتياطي وضمانات المحاكمة العادلة ومدة الـ 24 ساعة للشرطة القضائية)
        - قانون الجرائم والعقوبات اليمني
        - القانون المدني وقانون المرافعات
        - قانون السجون ولائحته التنظيمية (حقوق السجين، وضوابط الإفراج، إلخ)
        
        صغ دائماً تقريرك بلغة قانونية رصينة وموضوعية، مبيناً بوضوح المخالفات والتجاوزات القانونية المسجلة، واقتراح المواد القانونية الدقيقة التي تعزز النزاهة والعدالة.
    """

    suspend fun consult(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "خطأ: لم يتم تهيئة مفتاح واجهة برمجة تطبيقات Gemini (GEMINI_API_KEY) في لوحة الأسرار (Secrets panel) للتطبيق. يرجى تهيئته لتفعيل ذكاء المساعد القضائي."
        }
        
        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(temperature = 0.4f),
            systemInstruction = Content(parts = listOf(Part(text = SYSTEM_ROLE)))
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "خطأ: لم يتم تلقي أي رد من المستشار القضائي الذكي."
        } catch (e: Exception) {
            "خطأ أثناء استدعاء المستشار القضائي الذكي: ${e.localizedMessage}"
        }
    }
}

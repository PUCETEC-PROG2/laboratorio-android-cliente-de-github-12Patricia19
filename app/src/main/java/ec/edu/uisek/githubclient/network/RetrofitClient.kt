package ec.edu.uisek.githubclient.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val BASE_URL = "https://api.github.com/"
    private const val GITHUB_TOKEN = "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"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val apiService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
    
    fun getAuthToken(): String {
        return if (GITHUB_TOKEN.isNotEmpty()) {
            "token $GITHUB_TOKEN"
        } else {
            ""
        }
    }
    
    fun hasToken(): Boolean {
        return GITHUB_TOKEN.isNotEmpty()
    }
}

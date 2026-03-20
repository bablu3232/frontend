package com.simats.drugssearch.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Updated to your machine's IP address (from ipconfig)
    private const val BASE_URL = "http://14.139.187.229:8081/jan2026/drugsearch/"
    const val IMAGE_BASE_URL = "http://14.139.187.229:8081/jan2026/drugsearch/"
    
    // NOTE: If your IP changes (e.g., reconnecting Wi-Fi), you must update this!

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        val gson = com.google.gson.GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(ApiService::class.java)
    }
}

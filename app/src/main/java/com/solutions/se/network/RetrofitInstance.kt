package com.solutions.se.network

import com.solutions.se.api.NotificationApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.solutions.se.api.ServicioApi


object RetrofitInstance {

    private const val BASE_URL_SERVICIOS = "http://192.168.100.116:1003/api/"
    private const val BASE_URL_NOTIFICATIONS = "http://192.168.100.116:1008/api/"

    private val client by lazy {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    val servicioApi: ServicioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SERVICIOS)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ServicioApi::class.java)
    }

    val notificationApi: NotificationApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_NOTIFICATIONS)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NotificationApi::class.java)
    }
}

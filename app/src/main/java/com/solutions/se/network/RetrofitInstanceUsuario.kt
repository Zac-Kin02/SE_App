package com.solutions.se.network

import com.solutions.se.api.UsuarioApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceUsuario {
    private const val BASE_URL_USUARIO = "http://192.168.100.116:1001/api/"

    val api: UsuarioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_USUARIO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuarioApi::class.java)
    }
}

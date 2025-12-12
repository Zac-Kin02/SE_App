package com.solutions.se.network

import com.solutions.se.api.DescuentoApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceDescuento {
    private const val URL_DESCUENTO = "http://192.168.100.116:1004/api/"

    val api: DescuentoApi by lazy {
        Retrofit.Builder()
            .baseUrl(URL_DESCUENTO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DescuentoApi::class.java)
    }
}

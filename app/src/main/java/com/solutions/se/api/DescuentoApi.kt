package com.solutions.se.api

import com.solutions.se.model.Descuento
import retrofit2.http.GET
import retrofit2.http.Path

interface DescuentoApi {
    @GET("descuentos/codigo/{codigo}")
    suspend fun obtenerDescuentoPorCodigo(@Path("codigo") codigo: String): Descuento
}

package com.solutions.se.api

import com.solutions.se.model.Servicio
import retrofit2.Response
import retrofit2.http.*

interface ServicioApi {

    @GET("servicios")
    suspend fun getServicios(): List<Servicio>

    @GET("servicios/{id}")
    suspend fun getServicioById(@Path("id") id: Long): Response<Servicio>

    @POST("servicios")
    suspend fun crearServicio(@Body servicio: Servicio): Response<Servicio>

    @PUT("servicios/{id}")
    suspend fun actualizarServicio(@Path("id") id: Long, @Body servicio: Servicio): Response<Servicio>

    @DELETE("servicios/{id}")
    suspend fun eliminarServicio(@Path("id") id: Long): Response<Unit>

    @GET("servicios/{id}")
    suspend fun getServicioPorId(@Path("id") id: Long): Servicio

    @PUT("servicios/{id}")
    suspend fun editarServicio(
        @Path("id") id: Long,
        @Body body: Map<String, @JvmSuppressWildcards Any>,
        @Header("rol") rol: String
    ): Servicio




}

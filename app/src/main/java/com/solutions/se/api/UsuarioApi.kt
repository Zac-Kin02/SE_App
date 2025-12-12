package com.solutions.se.api

import com.solutions.se.model.entity.Usuario
import com.solutions.se.model.UsuarioUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {

    @GET("usuarios/byCorreo")
    suspend fun getUsuarioByCorreo(@Query("correo") correo: String): Response<Usuario>

    @POST("usuarios")
    suspend fun crearUsuario(@Body usuario: Usuario): Response<Usuario>

    @POST("usuarios/login")
    suspend fun login(@Body credenciales: Map<String, String>): Response<Usuario>



    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: UsuarioUpdate
    ): Response<Void>


}

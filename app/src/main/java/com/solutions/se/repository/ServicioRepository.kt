package com.solutions.se.repository

import com.solutions.se.api.ServicioApi
import com.solutions.se.model.Servicio
import retrofit2.Response

class ServicioRepository(private val api: ServicioApi) {

    suspend fun obtenerServicios(): List<Servicio> {
        return try {
            api.getServicios()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun crearServicio(servicio: Servicio): Response<Servicio> {
        return try {
            api.crearServicio(servicio)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Error al crear servicio"))
        }
    }

    suspend fun actualizarServicio(id: Long, servicio: Servicio): Response<Servicio> {
        return try {
            api.actualizarServicio(id, servicio)
        } catch (e: Exception) {
            Response.error(500, okhttp3.ResponseBody.create(null, "Error al actualizar servicio"))
        }
    }

    suspend fun eliminarServicio(id: Long): Boolean {
        return try {
            val response = api.eliminarServicio(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

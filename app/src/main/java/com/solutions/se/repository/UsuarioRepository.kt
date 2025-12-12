package com.solutions.se.repository

import com.solutions.se.api.UsuarioApi
import com.solutions.se.dao.UsuarioDao
import com.solutions.se.model.entity.Usuario
import com.solutions.se.model.UsuarioUpdate
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class UsuarioRepository(
    private val api: UsuarioApi?,
    private val usuarioDao: UsuarioDao
) {

    suspend fun login(correo: String, contrasena: String): Usuario? {
        return try {
            val response = api?.login(mapOf("correo" to correo, "contrasena" to contrasena))

            if (response != null && response.isSuccessful) {
                val usuario = response.body()
                if (usuario != null) {
                    usuarioDao.eliminarUsuarioLocal()
                    usuarioDao.insertUsuario(usuario)
                }
                usuario
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    suspend fun crearUsuario(usuario: Usuario): Response<Usuario> {
        return try {
            val response = api?.crearUsuario(usuario)
            if (response != null && response.isSuccessful) {
                usuarioDao.eliminarUsuarioLocal()
                usuarioDao.insertUsuario(response.body()!!)
                response
            } else {
                Response.error(500, "Error al crear usuario".toResponseBody(null))
            }
        } catch (e: Exception) {
            Response.error(500, "Error de red".toResponseBody(null))
        }
    }

    suspend fun getUsuarioPorCorreo(correo: String): Usuario? {
        return try {
            val response = api?.getUsuarioByCorreo(correo)
            if (response != null && response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    usuarioDao.eliminarUsuarioLocal()
                    usuarioDao.insertUsuario(user)
                }
                user
            } else {
                usuarioDao.getUsuarioPorCorreo(correo)
            }
        } catch (e: Exception) {
            usuarioDao.getUsuarioPorCorreo(correo)
        }
    }

    suspend fun actualizarUsuario(usuarioUpdate: UsuarioUpdate): Boolean {
        return try {
            val response = api?.actualizarUsuario(usuarioUpdate.idUsuario, usuarioUpdate)
            if (response != null && response.isSuccessful) {
                val usuarioActualizado = Usuario(
                    idUsuario = usuarioUpdate.idUsuario,
                    nombre = usuarioUpdate.nombre,
                    apellido = usuarioUpdate.apellido,
                    correo = usuarioUpdate.correo,
                    telefono = usuarioUpdate.telefono,
                    direccion = usuarioUpdate.direccion,
                    contrasena = usuarioUpdate.contrasena,
                    rol = usuarioUpdate.rol
                )
                usuarioDao.eliminarUsuarioLocal()
                usuarioDao.insertUsuario(usuarioActualizado)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }


    suspend fun getUsuarioLocal(): Usuario? = usuarioDao.getUsuarioLocal()

    suspend fun eliminarUsuarioLocal() = usuarioDao.eliminarUsuarioLocal()
}

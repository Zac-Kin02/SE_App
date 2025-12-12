package com.solutions.se.dao

import androidx.room.*

import com.solutions.se.model.entity.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuario LIMIT 1")
    suspend fun getUsuarioLocal(): Usuario?

    @Query("DELETE FROM usuario")
    suspend fun eliminarUsuarioLocal()

    @Update
    suspend fun updateUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE correo = :correo LIMIT 1")
    suspend fun getUsuarioPorCorreo(correo: String): Usuario?
}

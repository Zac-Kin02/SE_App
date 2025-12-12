package com.solutions.se.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey
    val idUsuario: Long? = null,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val contrasena: String,
    val rol: String
)

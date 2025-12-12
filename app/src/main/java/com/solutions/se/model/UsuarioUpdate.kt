package com.solutions.se.model

data class UsuarioUpdate(
    val idUsuario: Long,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val contrasena: String,
    val rol: String
)


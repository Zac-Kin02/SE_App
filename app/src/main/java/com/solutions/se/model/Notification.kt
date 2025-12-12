package com.solutions.se.model

data class Notification(
    val idNotificacion: Int,
    val idUsuario: Int,
    val mensaje: String,
    val fechaEnvio: String,
    val estado: String
)

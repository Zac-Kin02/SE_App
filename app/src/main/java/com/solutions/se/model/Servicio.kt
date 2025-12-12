package com.solutions.se.model

data class Servicio(
    val idServicio: Int,
    val nombreServicio: String,
    val descripcion: String,
    val precioBase: Int,
    val imagenResId: Int = 0
)
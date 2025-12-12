package com.solutions.se.model

data class Descuento(
    val idDescuento: Long,
    val nombre: String,
    val descuento: Double,
    val condicion: String?,
    val codigo: String
)

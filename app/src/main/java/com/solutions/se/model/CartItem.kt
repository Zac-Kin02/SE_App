package com.solutions.se.model

import java.util.UUID

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val cantidad: Int = 1,
    val precio: Int,
    val estrellas: Int,
    val imagenRes: Int
)

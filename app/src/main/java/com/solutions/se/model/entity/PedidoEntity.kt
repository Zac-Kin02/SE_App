package com.solutions.se.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val idPedido: Int = 0,
    val producto: String,
    val cantidad: Int,
    val descripcion: String,
    val estrellas: Int,
    val estado: String,
    val fecha: String,
    val imagenRes: Int
)
package com.solutions.se.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.solutions.se.R
import com.solutions.se.view.Pedido

class PedidosViewModel : ViewModel() {
    private val _pedidos = mutableStateListOf<Pedido>()


    fun agregarPedido(producto: String, cantidad: Int, total: Double) {
        val nuevoPedido = Pedido(
            id = _pedidos.size + 1,
            producto = producto,
            cantidad = cantidad,
            descripcion = "Compra simulada por \$${"%.2f".format(total)}",
            estrellas = 0,
            estado = "En camino",
            fecha = "26/10/2025",
            imagenRes = R.drawable.nose
        )
        _pedidos.add(0, nuevoPedido)
    }
}
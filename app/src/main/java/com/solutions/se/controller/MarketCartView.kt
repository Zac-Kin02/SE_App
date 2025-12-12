package com.solutions.se.controller

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.solutions.se.db.AppDatabase
import com.solutions.se.model.CartItem
import com.solutions.se.model.entity.PedidoEntity
import com.solutions.se.view.Pedido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MarketCartView(application: Application) : AndroidViewModel(application) {

    var cartItems = mutableStateListOf<CartItem>()
        private set

    var historialPedidos = mutableStateListOf<Pedido>()
        private set

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "se_database"
    ).build()

    private val pedidoDao = db.pedidoDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val pedidosGuardados = pedidoDao.getAllPedidos()
            val lista = pedidosGuardados.map {
                Pedido(
                    id = it.idPedido,
                    producto = it.producto,
                    cantidad = it.cantidad,
                    descripcion = it.descripcion,
                    estrellas = it.estrellas,
                    estado = it.estado,
                    fecha = it.fecha,
                    imagenRes = it.imagenRes
                )
            }

            withContext(Dispatchers.Main) {
                historialPedidos.addAll(lista)
            }
        }
    }

    fun addToCart(item: CartItem) {
        val existingItem = cartItems.find { it.nombre == item.nombre }
        if (existingItem != null) {
            val index = cartItems.indexOf(existingItem)
            cartItems[index] = existingItem.copy(cantidad = existingItem.cantidad + 1)
        } else {
            cartItems.add(item)
        }
    }

    fun removeFromCart(item: CartItem) {
        cartItems.remove(item)
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun completePurchase() {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL")).format(Date())

        viewModelScope.launch(Dispatchers.IO) {
            cartItems.forEach { item ->
                val pedidoEntity = PedidoEntity(
                    producto = item.nombre,
                    cantidad = item.cantidad,
                    descripcion = "Pedido realizado",
                    estrellas = item.estrellas,
                    estado = "Pendiente",
                    fecha = fecha,
                    imagenRes = item.imagenRes
                )
                pedidoDao.insertPedido(pedidoEntity)
            }

            updateHistorialFromDb()
            clearCart()
        }
    }

    fun agregarCompraQR(mensaje: String) {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL")).format(Date())

        viewModelScope.launch(Dispatchers.IO) {
            cartItems.forEach { item ->
                val pedidoEntity = PedidoEntity(
                    producto = item.nombre,
                    cantidad = item.cantidad,
                    descripcion = mensaje,
                    estrellas = item.estrellas,
                    estado = "Confirmado QR",
                    fecha = fecha,
                    imagenRes = item.imagenRes
                )
                pedidoDao.insertPedido(pedidoEntity)
            }

            updateHistorialFromDb()
            clearCart()
        }
    }

    private fun updateHistorialFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val pedidosActualizados = pedidoDao.getAllPedidos()
            val lista = pedidosActualizados.map {
                Pedido(
                    id = it.idPedido,
                    producto = it.producto,
                    cantidad = it.cantidad,
                    descripcion = it.descripcion,
                    estrellas = it.estrellas,
                    estado = it.estado,
                    fecha = it.fecha,
                    imagenRes = it.imagenRes
                )
            }

            withContext(Dispatchers.Main) {
                historialPedidos.clear()
                historialPedidos.addAll(lista)
            }
        }
    }
}

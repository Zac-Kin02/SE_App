package com.solutions.se.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solutions.se.controller.MarketCartView

data class Pedido(
    val id: Int,
    val producto: String,
    val cantidad: Int,
    val descripcion: String,
    val estrellas: Int,
    val estado: String,
    val fecha: String,
    val imagenRes: Int
)

@Composable
fun HistorialPedidosView(cartViewModel: MarketCartView) {
    val pedidos = cartViewModel.historialPedidos
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Pedidos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colores.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(pedidos) { pedido ->
                PedidoCard(pedido)
            }
        }
    }
}

@Composable
fun PedidoCard(pedido: Pedido) {
    val colores = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = pedido.imagenRes),
                contentDescription = pedido.producto,
                modifier = Modifier
                    .size(70.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pedido.producto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colores.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pedido.descripcion,
                    fontSize = 13.sp,
                    color = colores.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Cantidad: ${pedido.cantidad}",
                    fontSize = 13.sp,
                    color = colores.primary
                )
                Text(
                    text = "Estado: ${pedido.estado}",
                    fontSize = 13.sp,
                    color = when (pedido.estado) {
                        "Entregado" -> Color(0xFF4CAF50)
                        "Pendiente" -> Color(0xFFFFC107)
                        else -> colores.primary
                    },
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Fecha: ${pedido.fecha}",
                    fontSize = 12.sp,
                    color = colores.onSurface.copy(alpha = 0.6f)
                )
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    repeat(pedido.estrellas) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

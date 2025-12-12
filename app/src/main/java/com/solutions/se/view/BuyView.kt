package com.solutions.se.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solutions.se.controller.MarketCartView
import com.solutions.se.model.CartItem
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BuyView(navController: NavController, cartViewModel: MarketCartView) {
    val cartItems = cartViewModel.cartItems
    val total = cartItems.sumOf { it.precio * it.cantidad }.toDouble()
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Carrito de Compras",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colores.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(cartItems, key = { it.id }) { item ->
                CartItemCard(
                    item = item,
                    onItemChange = { updatedItem ->
                        val index = cartItems.indexOfFirst { it.id == updatedItem.id }
                        if (index != -1) cartItems[index] = updatedItem
                    },
                    onRemove = { cartViewModel.removeFromCart(item) } // ← ARREGLADO
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colores.onSurface)
            val formattedTotal = NumberFormat.getNumberInstance(Locale("es", "CL")).format(total)
            Text("${formattedTotal} CLP", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colores.onSurface)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                cartViewModel.completePurchase()
                cartViewModel.agregarCompraQR("Pago normal confirmado")
                navController.navigate("payment/$total")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colores.secondary)
        ) {
            Text("Pagar", color = colores.onPrimary, fontSize = 18.sp)
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onItemChange: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit
) {
    val colores = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colores.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imagenRes),
                contentDescription = item.nombre,
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colores.onSurface)

                val formattedPrice = NumberFormat.getNumberInstance(Locale("es", "CL")).format(item.precio)
                Text("Precio: $formattedPrice CLP", fontSize = 14.sp, color = colores.primary)

                Row {
                    repeat(item.estrellas) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            tint = Color(0xFFFFC107),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (item.cantidad > 1) onItemChange(item.copy(cantidad = item.cantidad - 1)) }) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = colores.primary)
                    }

                    Text("${item.cantidad}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colores.onSurface)

                    IconButton(onClick = { onItemChange(item.copy(cantidad = item.cantidad + 1)) }) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = colores.primary)
                    }
                }

                IconButton(onClick = { onRemove(item) }) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }
        }
    }
}

package com.solutions.se.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solutions.se.R
import com.solutions.se.controller.MarketCartView
import com.solutions.se.model.Servicio
import com.solutions.se.network.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import com.solutions.se.model.CartItem

@Composable
fun MarketplaceView(navController: NavController, cartViewModel: MarketCartView, isDarkTheme: Boolean) {
    var searchText by remember { mutableStateOf("") }
    var servicios by remember { mutableStateOf<List<Servicio>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val backgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.background else Color(0xFFF5F5F5)
    val cardColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color.White
    val accentBlue = if (isDarkTheme) Color(0xFF64B5F6) else Color(0xFF1565C0)
    val accentGreen = if (isDarkTheme) Color(0xFF81C784) else Color(0xFF2E7D32)
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF1C1C1C)
    val textSecondary = if (isDarkTheme) Color(0xFFB0BEC5) else Color(0xFF5F6368)

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitInstance.servicioApi.getServicios()
                servicios = response.map { servicio ->
                    val imagenRes = when (servicio.idServicio) {
                        4 -> R.drawable.hagroyi12
                        5 -> R.drawable.cercado_estandar
                        6 -> R.drawable.cercado_reforzado
                        7 -> R.drawable.alarma_residencial_alambrica
                        8 -> R.drawable.alarma_comercial_alambrica
                        9 -> R.drawable.kit_4_camaras
                        10 -> R.drawable.kit_8_camaras
                        11 -> R.drawable.mantenimiendo
                        12 -> R.drawable.revision
                        13 -> R.drawable.centurion_d10_smart
                        14 -> R.drawable.kit_dahua_16_camaras
                        else -> R.drawable.nose
                    }
                    servicio.copy(imagenResId = imagenRes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "Marketplace",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = accentBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Buscar servicios...", color = textSecondary) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = cardColor,
                unfocusedContainerColor = cardColor,
                focusedIndicatorColor = accentBlue,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = textPrimary,
                unfocusedTextColor = textPrimary,
                cursorColor = accentBlue
            )
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentBlue)
            }
        } else {
            val serviciosFiltrados = servicios.filter {
                it.nombreServicio.contains(searchText, ignoreCase = true) ||
                        it.descripcion.contains(searchText, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(serviciosFiltrados) { servicio ->
                    var expanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = servicio.imagenResId),
                                contentDescription = servicio.nombreServicio,
                                modifier = Modifier
                                    .size(90.dp)
                                    .padding(8.dp),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = servicio.nombreServicio,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )

                                if (expanded) {
                                    Text(
                                        text = servicio.descripcion,
                                        fontSize = 14.sp,
                                        color = textSecondary
                                    )
                                }

                                val formattedPrice = NumberFormat.getNumberInstance(Locale("es", "CL"))
                                    .format(servicio.precioBase)
                                Text(
                                    text = "$formattedPrice CLP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentGreen
                                )

                                Row {
                                    repeat(5) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Estrella",
                                            tint = Color(0xFFFFC107),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = {
                                val item = CartItem(
                                    nombre = servicio.nombreServicio,
                                    cantidad = 1,
                                    precio = servicio.precioBase,
                                    estrellas = 5,
                                    imagenRes = servicio.imagenResId
                                )

                                cartViewModel.addToCart(item)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AddShoppingCart,
                                    contentDescription = "Agregar al carrito",
                                    tint = accentBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

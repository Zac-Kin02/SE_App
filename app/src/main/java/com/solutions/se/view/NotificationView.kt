package com.solutions.se.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solutions.se.model.Notification
import com.solutions.se.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun NotificationView(isDarkTheme: Boolean) {
    var textoBusqueda by remember { mutableStateOf("") }
    var notificaciones by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val fondo = if (isDarkTheme) Color(0xFF121212) else Color(0xFFF4F4F4)
    val tarjeta = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val azul = Color(0xFF2196F3)
    val textoPrincipal = if (isDarkTheme) Color.White else Color(0xFF1E1E1E)
    val textoSecundario = if (isDarkTheme) Color(0xFFB0BEC5) else Color(0xFF757575)

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val respuesta = RetrofitInstance.notificationApi.getNotificaciones()
                notificaciones = respuesta
            } catch (e: Exception) {
                mensajeError = "Error al cargar notificaciones: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
            .padding(16.dp)
    ) {
        Text(
            text = "Notificaciones",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = azul
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            placeholder = { Text("Buscar notificaciones...", color = textoSecundario) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = tarjeta,
                unfocusedContainerColor = tarjeta,
                focusedIndicatorColor = azul,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = textoPrincipal,
                unfocusedTextColor = textoPrincipal,
                cursorColor = azul
            )
        )

        when {
            cargando -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = azul)
            }

            mensajeError != null -> Text(
                text = mensajeError!!,
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            else -> {
                val filtradas = notificaciones.filter {
                    it.mensaje.contains(textoBusqueda, ignoreCase = true) ||
                            it.estado.contains(textoBusqueda, ignoreCase = true)
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtradas) { notificacion ->
                        TarjetaNotificacion(notificacion, tarjeta, textoPrincipal, textoSecundario)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaNotificacion(
    notificacion: Notification,
    colorTarjeta: Color,
    textoPrincipal: Color,
    textoSecundario: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorTarjeta),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = notificacion.mensaje,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textoPrincipal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estado: ${notificacion.estado}",
                color = textoSecundario,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Enviado: ${notificacion.fechaEnvio.replace("T", " ")}",
                color = textoSecundario,
                fontSize = 12.sp
            )
        }
    }
}

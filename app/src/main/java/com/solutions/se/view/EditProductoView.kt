package com.solutions.se.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductoView(
    navController: NavController,
    idServicio: Int
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("Nombre del Servicio $idServicio") }
    var descripcion by remember { mutableStateOf("Descripción del servicio $idServicio") }
    var precio by remember { mutableStateOf("1000") }

    var guardando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var eliminado by remember { mutableStateOf(false) } // Para simular eliminación

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->

        if (eliminado) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¡Producto eliminado!", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Servicio") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it.filter { ch -> ch.isDigit() || ch == '.' } },
                label = { Text("Precio CLP") },
                modifier = Modifier.fillMaxWidth()
            )

            if (mensaje.isNotEmpty()) {
                Text(mensaje, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || descripcion.isBlank() || precio.isBlank()) {
                        mensaje = "Completa todos los campos"
                        return@Button
                    }
                    val precioDouble = precio.toDoubleOrNull()
                    if (precioDouble == null) {
                        mensaje = "Precio inválido"
                        return@Button
                    }

                    guardando = true
                    mensaje = "Guardando..."
                    scope.launch {
                        delay(1500)
                        guardando = false
                        mensaje = "¡Cambios guardados exitosamente!"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !guardando
            ) {
                Text(if (guardando) "Guardando..." else "Guardar Cambios")
            }

            Button(
                onClick = {
                    guardando = true
                    mensaje = "Eliminando..."
                    scope.launch {
                        delay(1500)
                        guardando = false
                        eliminado = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar Producto", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}

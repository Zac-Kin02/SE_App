package com.solutions.se.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solutions.se.model.Servicio
import com.solutions.se.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductosView(navController: NavController) {

    val scope = rememberCoroutineScope()
    var productos by remember { mutableStateOf<List<Servicio>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) { RetrofitInstance.servicioApi.getServicios() }
            productos = response
        } catch (_: Exception) { }
    }

    var visibleProductos by remember { mutableStateOf(productos) }

    LaunchedEffect(productos) {
        visibleProductos = productos
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {  },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Productos", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(visibleProductos, key = { it.idServicio }) { servicio ->
                    var isDeleted by remember { mutableStateOf(false) }

                    if (!isDeleted) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        servicio.nombreServicio,
                                        fontSize = 16.sp,
                                        maxLines = 1
                                    )
                                    Text(
                                        "${servicio.precioBase} CLP",
                                        fontSize = 14.sp
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            navController.navigate("editProducto/${servicio.idServicio}")
                                        },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("Editar", fontSize = 14.sp)
                                    }

                                    IconButton(
                                        onClick = {
                                            isDeleted = true
                                            scope.launch {
                                                delay(2000)
                                                isDeleted = false
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

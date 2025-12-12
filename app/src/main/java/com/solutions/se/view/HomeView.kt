package com.solutions.se.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.solutions.se.R
import com.solutions.se.controller.MarketCartView
import com.solutions.se.model.Servicio
import com.solutions.se.network.RetrofitInstance
import com.solutions.se.repository.ThemePreferences
import com.solutions.se.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavController,
    temaOscuro1: Boolean,
    onThemeChange: (Boolean) -> Unit,
    cartViewModel: MarketCartView,
    rolUsuario: String
) {
    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val scope = rememberCoroutineScope()
    var temaOscuro by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        themePreferences.darkThemeFlow.collectLatest {
            temaOscuro = it
        }
    }

    fun cambiarTema(nuevo: Boolean) {
        temaOscuro = nuevo
        scope.launch { themePreferences.setDarkTheme(nuevo) }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todos") }

    val filtros = listOf("Todos", "Cercado", "Cámara", "Servicio mantenimiento", "Motores")
    val ofertas = listOf(R.drawable.hagroy_logo, R.drawable.centurion_logo, R.drawable.dahua_logo)

    var servicios by remember { mutableStateOf<List<Servicio>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) { RetrofitInstance.servicioApi.getServicios() }
            val destacados = response.filter { it.idServicio in listOf(4, 11, 12, 13, 14) }
            servicios = destacados.map { servicio ->
                val imagenRes = when (servicio.idServicio) {
                    4 -> R.drawable.hagroyi12
                    11 -> R.drawable.mantenimiendo
                    12 -> R.drawable.revision
                    13 -> R.drawable.centurion_d10_smart
                    14 -> R.drawable.kit_dahua_16_camaras
                    else -> R.drawable.nose
                }
                servicio.copy(imagenResId = imagenRes)
            }
        } catch (_: Exception) {}
    }

    val fondo = if (temaOscuro) fondoPrincipalOscuro else fondoPrincipalClaro
    val textoDrawer = if (temaOscuro) textoPrincipalOscuro else textoPrincipalClaro

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.50f)
                    .fillMaxHeight()
                    .background(colorPrimario)
            ) {
                DrawerMenu(
                    navController = navController,
                    drawerState = drawerState,
                    darkTheme = temaOscuro,
                    onThemeChange = { cambiarTema(it) },
                    rol = rolUsuario,
                    textoColor = textoDrawer
                )
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = colorPrimario,
                    onClick = { navController.navigate("adminProductos") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "", tint = Color.White)
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text("Buscar...", color = if (temaOscuro) textoSecundarioOscuro else textoSecundarioClaro)
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.90f)
                                .clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = if (temaOscuro) fondoTarjetaOscuro else fondoTarjetaClaro,
                                unfocusedContainerColor = if (temaOscuro) fondoTarjetaOscuro else fondoTarjetaClaro,
                                focusedIndicatorColor = colorPrimario,
                                unfocusedIndicatorColor = colorSecundario
                            ),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = if (temaOscuro) textoPrincipalOscuro else textoPrincipalClaro
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("perfil") }) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colorPrimario)
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(fondo)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(filtros.size) { index ->
                            val filtro = filtros[index]
                            FilterChip(
                                selected = selectedFilter == filtro,
                                onClick = { selectedFilter = filtro },
                                label = { Text(filtro, color = textoDrawer) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorPrimario,
                                    selectedLabelColor = Color.White,
                                    containerColor = colorSecundario,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        items(ofertas.size) { index ->
                            Image(
                                painter = rememberAsyncImagePainter(ofertas[index]),
                                contentDescription = null,
                                modifier = Modifier
                                    .width(250.dp)
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    val serviciosFiltrados = servicios.filter { servicio ->
                        val coincideTexto = servicio.nombreServicio.contains(searchQuery, ignoreCase = true)
                        val coincideFiltro = when (selectedFilter) {
                            "Todos" -> true
                            "Cercado" -> servicio.nombreServicio.contains("cercado", ignoreCase = true)
                            "Cámara" -> servicio.nombreServicio.contains("cámara", ignoreCase = true)
                            "Servicio mantenimiento" -> servicio.nombreServicio.contains("mantenimiento", ignoreCase = true)
                            "Motores" -> servicio.nombreServicio.contains("motor", ignoreCase = true)
                            else -> true
                        }
                        coincideTexto && coincideFiltro
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(serviciosFiltrados) { servicio ->
                            ServicioCard(servicio, temaOscuro, cartViewModel)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ServicioCard(servicio: Servicio, darkTheme: Boolean, cartViewModel: MarketCartView) {
    var expandido by remember { mutableStateOf(false) }
    val fondoCard = if (darkTheme) fondoTarjetaOscuro else fondoTarjetaClaro
    val textoCard = if (darkTheme) textoPrincipalOscuro else textoPrincipalClaro
    val textoSec = if (darkTheme) textoSecundarioOscuro else textoSecundarioClaro

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(fondoCard)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = servicio.imagenResId),
                contentDescription = servicio.nombreServicio,
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Text(servicio.nombreServicio, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textoCard)

            val formato = NumberFormat.getNumberInstance(Locale("es", "CL"))
            val precioFormateado = formato.format(servicio.precioBase)

            Text(text = "$precioFormateado CLP", color = colorAcento, fontWeight = FontWeight.SemiBold)

            if (expandido) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(servicio.descripcion, fontSize = 13.sp, color = textoSec)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val item = com.solutions.se.model.CartItem(
                        nombre = servicio.nombreServicio,
                        cantidad = 1,
                        precio = servicio.precioBase,
                        estrellas = 5,
                        imagenRes = servicio.imagenResId
                    )
                    cartViewModel.addToCart(item)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorPrimario)
            ) {
                Icon(Icons.Default.AddShoppingCart, contentDescription = "", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Comprar", color = Color.White)
            }
        }
    }
}

@Composable
fun DrawerMenu(
    navController: NavController,
    drawerState: DrawerState,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    rol: String,
    textoColor: Color
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Menú", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textoColor)
        Spacer(modifier = Modifier.height(50.dp))

        DrawerItem("Inicio", textoColor) {
            scope.launch { drawerState.close() }
            navController.navigate("home")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tema Oscuro", color = textoColor)
            Switch(checked = darkTheme, onCheckedChange = onThemeChange)
        }

        Spacer(modifier = Modifier.height(24.dp))

        DrawerItem("Administrar Productos", textoColor) {
            scope.launch { drawerState.close() }
            navController.navigate("adminProductos")
        }
        Spacer(modifier = Modifier.height(24.dp))

        DrawerItem("Escanear QR", textoColor) {
            scope.launch { drawerState.close() }
            navController.navigate("qrScanner")
        }
    }
}

@Composable
fun DrawerItem(titulo: String, textoColor: Color, onClick: () -> Unit) {
    Text(
        text = titulo,
        fontSize = 20.sp,
        color = textoColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    )
}

package com.solutions.se

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.solutions.se.controller.MarketCartView
import com.solutions.se.ui.theme.TemaSE
import com.solutions.se.ui.theme.IconoBarraClaro
import com.solutions.se.ui.theme.IconoBarraOscuro
import com.solutions.se.view.*
import com.solutions.se.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val temaOscuro by themeViewModel.darkThemeFlow.collectAsState(initial = false)

            TemaSE(temaOscuro = temaOscuro) {
                MainScreen(
                    temaOscuro = temaOscuro,
                    onThemeChange = { themeViewModel.toggleDarkTheme(it) }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    temaOscuro: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != "login" && currentRoute != "register" && currentRoute != "qrScanner"
    var selectedItem by remember { mutableStateOf("home") }
    val cartViewModel: MarketCartView = viewModel()

    val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
    var rolUsuario by remember { mutableStateOf("") }

    LaunchedEffect(currentRoute) {
        rolUsuario = prefs.getString("rol", "") ?: ""
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val colorIconos = if (temaOscuro) IconoBarraOscuro else IconoBarraClaro
                BottomNavigationBar(
                    selectedItem = selectedItem,
                    onItemSelected = {
                        selectedItem = it
                        navController.navigate(it) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colorIcono = colorIconos
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isUserLoggedIn(context)) "home" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") { LoginView(navController) }
            composable("register") { RegisterView(navController) }
            composable("home") { HomeView(navController, temaOscuro, onThemeChange, cartViewModel, rolUsuario) }
            composable("adminProductos") { AdminProductosView(navController) }
            composable(
                route = "editProducto/{idServicio}",
                arguments = listOf(navArgument("idServicio") { type = NavType.IntType })
            ) { backStackEntry ->
                val idServicio = backStackEntry.arguments?.getInt("idServicio") ?: 0
                EditProductoView(navController = navController, idServicio = idServicio)
            }

            composable("marketplace") { MarketplaceView(navController, cartViewModel, temaOscuro) }
            composable("notificaciones") { NotificationView(temaOscuro) }
            composable("history") { HistorialPedidosView(cartViewModel) }
            composable("perfil") {
                ProfileView(
                    onBackClick = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
            composable("info") { AboutUsView() }
            composable("buy") { BuyView(navController, cartViewModel) }
            composable("payment/{total}") { backStackEntry ->
                val totalString = backStackEntry.arguments?.getString("total") ?: "0.0"
                val total = totalString.toDoubleOrNull() ?: 0.0
                VistaPago(navController = navController, total = total)
            }
            composable("qrScanner") {
                QrScannerScreen(
                    navController = navController,
                    onQrScanned = { scannedValue ->
                        cartViewModel.agregarCompraQR(scannedValue)
                        navController.popBackStack()
                    },
                    onClose = { navController.popBackStack() }
                )
            }

        }
    }
}

fun isUserLoggedIn(context: android.content.Context): Boolean {
    val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
    val savedEmail = prefs.getString("email", null)
    return !savedEmail.isNullOrBlank()
}

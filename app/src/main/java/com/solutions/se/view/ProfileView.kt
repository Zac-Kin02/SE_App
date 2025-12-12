package com.solutions.se.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solutions.se.R
import com.solutions.se.db.AppDatabase
import com.solutions.se.model.UsuarioUpdate
import com.solutions.se.model.entity.Usuario
import com.solutions.se.network.RetrofitInstanceUsuario
import com.solutions.se.repository.UsuarioRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val db = AppDatabase.getDatabase(context)
    val usuarioDao = db.usuarioDao()
    val usuarioRepository = remember { UsuarioRepository(RetrofitInstanceUsuario.api, usuarioDao) }

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var loading by remember { mutableStateOf(true) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val savedEmail = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        .getString("email", null)

    LaunchedEffect(savedEmail) {
        if (!savedEmail.isNullOrBlank()) {
            val userFromApi = try { usuarioRepository.getUsuarioPorCorreo(savedEmail) } catch (_: Exception) { null }
            val user = userFromApi ?: usuarioDao.getUsuarioPorCorreo(savedEmail)

            if (user != null) {
                usuario = user
                nombre = user.nombre
                apellido = user.apellido
                rol = user.rol.toString()
                correo = user.correo
                telefono = user.telefono
                direccion = user.direccion
                contrasena = ""
            }
        }
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3))
            )
        }
    ) { innerPadding ->
        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            usuario != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nose),
                            contentDescription = null,
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF2196F3), CircleShape)
                        )
                        FloatingActionButton(
                            onClick = {},
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(36.dp)
                                .offset(x = (-6).dp, y = (-6).dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    UserInfoEditRow("Nombre", nombre) { nombre = it }
                    UserInfoEditRow("Apellido", apellido) { apellido = it }

                    ReadOnlyInfoRow("Rol", rol)

                    UserInfoEditRow("Correo", correo) { correo = it }
                    UserInfoEditRow("Teléfono", telefono) { telefono = it }
                    UserInfoEditRow("Dirección", direccion) { direccion = it }
                    UserInfoEditRow("Contraseña", contrasena) { contrasena = it }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (contrasena.isBlank() || contrasena.length < 6) {
                                mensaje = "La contraseña debe tener mínimo 6 caracteres ❌"
                                return@Button
                            }

                            scope.launch {
                                try {
                                    val updatedUser = usuario!!.copy(
                                        nombre = nombre,
                                        apellido = apellido,
                                        correo = correo,
                                        telefono = telefono,
                                        direccion = direccion,
                                        contrasena = contrasena
                                    )

                                    usuarioDao.eliminarUsuarioLocal()
                                    usuarioDao.insertUsuario(updatedUser)

                                    val actualizadoRemoto = try {
                                        usuarioRepository.actualizarUsuario(
                                            UsuarioUpdate(
                                                idUsuario = updatedUser.idUsuario ?: 0L,
                                                nombre = updatedUser.nombre,
                                                apellido = updatedUser.apellido,
                                                correo = updatedUser.correo,
                                                telefono = updatedUser.telefono,
                                                direccion = updatedUser.direccion,
                                                contrasena = updatedUser.contrasena,
                                                rol = usuario!!.rol
                                            )
                                        )
                                    } catch (_: Exception) {
                                        false
                                    }


                                    mensaje = if (actualizadoRemoto) "Datos sincronizados con el servidor ✅"
                                    else "Datos guardados localmente 💾"
                                } catch (_: Exception) {
                                    mensaje = "Error al actualizar datos ❌"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar cambios")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = {
                        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().apply()
                        scope.launch { usuarioDao.eliminarUsuarioLocal() }
                        onLogout()
                    }) {
                        Text("Cerrar sesión")
                    }

                    if (mensaje != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mensaje!!,
                            color = if (mensaje!!.contains("❌")) Color.Red else Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            else -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontró el usuario", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun UserInfoEditRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Divider(color = Color(0xFFCCCCCC), thickness = 1.dp)
    }
}

@Composable
fun ReadOnlyInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Divider(color = Color(0xFFCCCCCC), thickness = 1.dp)
    }
}


package com.solutions.se.view

import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.solutions.se.db.AppDatabase
import com.solutions.se.model.entity.Usuario
import com.solutions.se.network.RetrofitInstanceUsuario
import com.solutions.se.repository.UsuarioRepository
import kotlinx.coroutines.launch

@Composable
fun RegisterView(navController: NavController) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val usuarioRepository = remember { UsuarioRepository(RetrofitInstanceUsuario.api, db.usuarioDao()) }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val correoValido = remember(correo) {
        Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    val formularioValido = nombre.isNotEmpty() &&
            correoValido &&
            contrasena.length >= 6 &&
            confirmarContrasena == contrasena

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Número de teléfono (opcional)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección (opcional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        if (mensajeError != null) {
            Text(
                text = mensajeError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                scope.launch {
                    cargando = true
                    try {
                        val usuario = Usuario(
                            idUsuario = null,
                            nombre = nombre,
                            apellido = apellido,
                            correo = correo,
                            contrasena = contrasena,
                            telefono = telefono.ifBlank { "No especificado" },
                            direccion = direccion.ifBlank { "No especificada" },
                            rol = "USER"
                        )

                        val respuesta = usuarioRepository.crearUsuario(usuario)

                        if (respuesta.isSuccessful) {
                            saveLoginState(context, correo)
                            navController.navigate("home") {
                                popUpTo("register") { inclusive = true }
                            }
                        } else {
                            mensajeError = respuesta.errorBody()?.string() ?: "Error al registrar usuario"
                        }
                    } catch (e: Exception) {
                        mensajeError = "Error inesperado: ${e.localizedMessage}"
                    } finally {
                        cargando = false
                    }
                }
            },
            enabled = formularioValido && !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¿Ya tienes una cuenta? Inicia sesión",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        )
    }
}

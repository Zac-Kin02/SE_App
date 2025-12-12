package com.solutions.se.view

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import com.solutions.se.network.RetrofitInstanceUsuario
import com.solutions.se.repository.UsuarioRepository
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

@Composable
fun LoginView(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val db = AppDatabase.getDatabase(context)
    val usuarioRepository = remember { UsuarioRepository(RetrofitInstanceUsuario.api, db.usuarioDao()) }

    val savedEmail = getSavedLogin(context)
    LaunchedEffect(Unit) {
        if (savedEmail != null) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val isEmailValid = remember(email) {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colores.primary
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico", color = colores.onSurface) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colores.primary,
                unfocusedBorderColor = colores.outline,
                cursorColor = colores.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = colores.onSurface) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colores.primary,
                unfocusedBorderColor = colores.outline,
                cursorColor = colores.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    errorMessage = null
                    try {
                        val usuario = usuarioRepository.login(email, password)

                        if (usuario != null) {
                            saveLoginState(context, email)
                            db.usuarioDao().eliminarUsuarioLocal()
                            db.usuarioDao().insertUsuario(usuario)

                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            Log.d("LoginView", "Inicio de sesión exitoso para $email")

                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            errorMessage = "Correo o contraseña incorrectos"
                        }

                    } catch (e: Exception) {
                        errorMessage = "Error al conectar con el servidor"
                        Log.e("LoginView", "Error: ${e.message}", e)
                    }
                    loading = false
                }
            },
            enabled = isEmailValid && password.isNotEmpty() && !loading,
            colors = ButtonDefaults.buttonColors(containerColor = colores.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(if (loading) "Verificando..." else "Iniciar Sesión", color = colores.onPrimary)
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = colores.error,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        OutlinedButton(
            onClick = {},
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colores.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Continuar con Google")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = colores.primary,
            modifier = Modifier.clickable {}
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "¿No tienes cuenta? Regístrate",
            color = colores.primary,
            modifier = Modifier.clickable { navController.navigate("register") }
        )
    }
}



fun saveLoginState(context: Context, email: String) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("email", email).apply()
}

fun getSavedLogin(context: Context): String? {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return prefs.getString("email", null)
}

fun clearLoginState(context: Context) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().clear().apply()
}

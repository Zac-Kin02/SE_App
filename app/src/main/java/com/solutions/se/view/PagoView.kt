package com.solutions.se.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.solutions.se.viewmodel.PedidosViewModel
import com.solutions.se.network.RetrofitInstanceDescuento
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun VistaPago(navController: NavController, total: Double) {

    val viewModel: PedidosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val scroll = rememberScrollState()
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaExp by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var codigoDescuento by remember { mutableStateOf("") }
    var porcentajeDescuento by remember { mutableStateOf(0.0) }

    val df = DecimalFormat("#,##0.00")
    val scope = rememberCoroutineScope()

    val correoValido = remember(correo) { android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() }
    val tarjetaValida = numeroTarjeta.filter { it.isDigit() }.length == 16
    val expValida = Regex("""^(0[1-9]|1[0-2])\/\d{2}$""").matches(fechaExp)
    val cvvValido = cvv.filter { it.isDigit() }.length == 3
    val datosCompradorValidos = nombre.isNotBlank() && apellido.isNotBlank() && direccion.isNotBlank() && correoValido
    val datosTarjetaValidos = tarjetaValida && expValida && cvvValido

    val descuento = total * porcentajeDescuento
    val totalFinal = total - descuento

    var mostrarDialogo by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var tituloDialogo by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect { entry ->
            val code = entry.savedStateHandle.get<String>("qrValue")
            if (!code.isNullOrBlank()) {
                codigoDescuento = code
                entry.savedStateHandle.remove<String>("qrValue")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scroll),
        horizontalAlignment = Alignment.Start
    ) {

        Text("Pago", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 12.dp))

        Text("Datos del comprador", fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp))
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(apellido, { apellido = it }, label = { Text("Apellido") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(correo, { correo = it }, label = { Text("Correo") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección") }, singleLine = false, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

        Spacer(modifier = Modifier.height(12.dp))

        Text("Datos de la tarjeta", fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp))
        OutlinedTextField(
            value = numeroTarjeta,
            onValueChange = { numeroTarjeta = it.filter { c -> c.isDigit() }.take(16) },
            label = { Text("Número de tarjeta (16 dígitos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fechaExp,
                onValueChange = {
                    val digitos = it.filter { c -> c.isDigit() }.take(4)
                    fechaExp = when {
                        digitos.length >= 3 -> digitos.substring(0, 2) + "/" + digitos.substring(2)
                        else -> digitos
                    }
                },
                label = { Text("MM/AA") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = cvv,
                onValueChange = { cvv = it.filter { c -> c.isDigit() }.take(3) },
                label = { Text("CVV") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(codigoDescuento, { codigoDescuento = it }, label = { Text("Código de descuento (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { navController.navigate("qrScanner") }, modifier = Modifier.weight(1f).height(45.dp)) {
                Text("Escanear QR")
            }

        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {
                scope.launch {
                    try {
                        if (codigoDescuento.isNotBlank()) {
                            val descuentoApi = RetrofitInstanceDescuento.api.obtenerDescuentoPorCodigo(codigoDescuento.trim())
                            porcentajeDescuento = descuentoApi.descuento
                            tituloDialogo = "Descuento aplicado"
                            mensajeDialogo = "Código válido (${(descuentoApi.descuento * 100).toInt()}% OFF)"
                            mostrarDialogo = true
                        } else {
                            porcentajeDescuento = 0.0
                            tituloDialogo = "Sin descuento"
                            mensajeDialogo = "No se ha ingresado código"
                            mostrarDialogo = true
                        }
                    } catch (e: Exception) {
                        porcentajeDescuento = 0.0
                        tituloDialogo = "Código inválido"
                        mensajeDialogo = "El código no es válido"
                        mostrarDialogo = true
                    }
                }
            }) {
                Text("Aplicar")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Resumen de pago", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Subtotal: \$${df.format(total)}")
                Text("Descuento: ${df.format(descuento)} (${(porcentajeDescuento * 100).toInt()}%)")
                Text("Total a pagar: \$${df.format(totalFinal)}", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!datosCompradorValidos) {
                    tituloDialogo = "Error"
                    mensajeDialogo = "Completa tus datos personales"
                    mostrarDialogo = true
                } else if (!datosTarjetaValidos) {
                    tituloDialogo = "Error"
                    mensajeDialogo = "Datos de tarjeta inválidos"
                    mostrarDialogo = true
                } else {
                    tituloDialogo = "Pago exitoso"
                    mensajeDialogo = "Compra procesada correctamente"
                    mostrarDialogo = true
                    viewModel.agregarPedido("Compra general", 1, totalFinal)
                    scope.launch {
                        delay(1200)
                        mostrarDialogo = false
                        navController.navigate("history") {
                            popUpTo("payment") { inclusive = true }
                        }
                    }
                }
            },
            enabled = datosCompradorValidos && datosTarjetaValidos,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Pagar \$${df.format(totalFinal)}", fontSize = 18.sp)
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text(tituloDialogo) },
                text = { Text(mensajeDialogo) },
                confirmButton = { TextButton(onClick = { mostrarDialogo = false }) { Text("Aceptar") } }
            )
        }
    }
}

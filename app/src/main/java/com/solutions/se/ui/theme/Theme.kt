package com.solutions.se.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val ColoresClaro = lightColorScheme(
    primary = colorPrimario,
    onPrimary = textoSobrePrimario,
    secondary = colorSecundario,
    background = fondoPrincipalClaro,
    surface = fondoTarjetaClaro,
    onSurface = textoSobreTarjeta
)

private val ColoresOscuro = darkColorScheme(
    primary = colorPrimarioOscuro,
    onPrimary = textoPrincipalOscuro,
    secondary = colorSecundario,
    background = fondoPrincipalOscuro,
    surface = fondoTarjetaOscuro,
    onSurface = textoSobreTarjetaOscuro
)

@Composable
fun TemaSE(
    temaOscuro: Boolean = false,
    contenido: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if(temaOscuro) ColoresOscuro else ColoresClaro,
        typography = Typography,
        content = contenido
    )
}


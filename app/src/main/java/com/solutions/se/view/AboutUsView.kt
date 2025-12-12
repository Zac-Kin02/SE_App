package com.solutions.se.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsView() {



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = "Logo SE",
            tint = Color(0xFF1976D2),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Soluciones Eléctricas SE",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = """
                En Soluciones Eléctricas SE nos especializamos en brindar servicios eléctricos de calidad, 
                priorizando la seguridad, eficiencia y satisfacción de nuestros clientes.
                
                Nuestro equipo está conformado por técnicos certificados y profesionales comprometidos 
                con ofrecer resultados confiables, rápidos y al mejor precio.
                
                ¡Gracias por confiar en nosotros!
            """.trimIndent(),
            fontSize = 16.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

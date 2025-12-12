package com.solutions.se.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    colorIcono: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio", modifier = Modifier.size(24.dp), tint = colorIcono) },
            selected = selectedItem == "home",
            onClick = { onItemSelected("home") },
            modifier = Modifier.weight(1f)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Buy", modifier = Modifier.size(24.dp), tint = colorIcono) },
            selected = selectedItem == "buy",
            onClick = { onItemSelected("buy") },
            modifier = Modifier.weight(1f)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Marketplace", modifier = Modifier.size(24.dp), tint = colorIcono) },
            selected = selectedItem == "marketplace",
            onClick = { onItemSelected("marketplace") },
            modifier = Modifier.weight(1f)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", modifier = Modifier.size(24.dp), tint = colorIcono) },
            selected = selectedItem == "notificaciones",
            onClick = { onItemSelected("notificaciones") },
            modifier = Modifier.weight(1f)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "Historial", modifier = Modifier.size(24.dp), tint = colorIcono) },
            selected = selectedItem == "history",
            onClick = { onItemSelected("history") },
            modifier = Modifier.weight(1f)
        )
    }
}


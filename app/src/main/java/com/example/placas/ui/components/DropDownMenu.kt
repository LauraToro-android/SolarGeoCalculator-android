package com.example.placas.ui.components


import android.text.Layout
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx. compose. material. icons. automirrored. filled. ExitToApp


@Composable
fun DropDownMenu(
    onSettingsClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

        Box( modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 16.dp),
            contentAlignment = Alignment.TopEnd)
        {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Abrir menú"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = (200).dp, y = 0.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Configuración") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Icono de Configuración",
                            tint = Color.Black
                        )
                    },
                    onClick = {
                        expanded = false
                        onSettingsClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Cerrar sesión") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons. AutoMirrored. Filled. ExitToApp,
                            contentDescription = "Icono de Cerrar Sesión",
                            tint = Color.Black
                        )
                    },
                    onClick = {
                        expanded = false
                        onLogOutClick()
                    }
                )
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun PreviewDropDownMenu() {
}
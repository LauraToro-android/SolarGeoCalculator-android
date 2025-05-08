package com.example.placas.ui.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.shadow



@Composable
fun inicioPantalla() {
    var items by rememberSaveable {
        mutableStateOf(
            listOf(
                "Frigorífico", "Lavadora", "Microondas", "Plancha",
                "Lavavajillas", "Horno", "Secadora"
            )
        )
    }

    var nuevoItem by rememberSaveable { mutableStateOf("") }
// fondo y colores que hay que cambiar
    val customContainerColor = Color(0xFFE8DFDF)
    val focusedBorderColor = Color(0xFF016E6E)
    val unfocusedBorderColor = Color.Gray
    val backgroundColor = Color(0xFFF0F4F8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)
    ) {
        // Títulos centrados
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lista de Electrodomésticos",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold, fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // zona para introducir el electrodoméstico
            TextField(
                value = nuevoItem,
                onValueChange = { nuevoItem = it },
                label = { Text("Nuevo Electrodoméstico") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = focusedBorderColor,
                    unfocusedIndicatorColor = unfocusedBorderColor,
                    focusedLabelColor = focusedBorderColor,
                    cursorColor = focusedBorderColor
                ),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de electrodomésticos con Cards estilizadas
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(items) { item ->
                    // Card con botón de eliminar y opción para editar
                    CCard(
                        item = item,
                        onDelete = { items = items - item }, // Eliminar item
                        onEdit = { newItem ->
                            // Editar el item, reemplazando el viejo por el nuevo
                            items = items.map { if (it == item) newItem else it }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(8.dp, shape = MaterialTheme.shapes.medium), // Sombra y borde redondeado
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = customContainerColor
                        )
                    )
                }
            }

            // Botón con icono y texto para agregar un electrodoméstico
            Button(
                onClick = {
                    if (nuevoItem.isNotBlank()) {
                        items = items + nuevoItem.trim()
                        nuevoItem = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Electrodoméstico")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar Electrodoméstico")
            }
        }
    }
}

@Composable
fun CCard(
    item: String,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier,
    shape: CornerBasedShape,
    colors: CardColors
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(item) }

    Card(
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isEditing) {
                TextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Editar Electrodoméstico") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF016E6E),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    shape = MaterialTheme.shapes.small
                )
                IconButton(
                    onClick = {
                        onEdit(editedText)
                        isEditing = false
                    }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Confirmar edición")
                }
            } else {
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { isEditing = true }
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                }
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInicioPantalla() {
        inicioPantalla()
    }


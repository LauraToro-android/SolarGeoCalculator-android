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

// Elemento para añadir a la futura base de datos
data class Electrodomestico(
    var nombre: String,
    var potencia: Float = 0f,
    var consumo: Float = 0f,
    var franjaHoraria: String = "" // Esta no sé si ponerlo como float o incluso añadir un boton para hacerlo mas visual
)

@Composable
fun inicioPantalla() {
    var items by rememberSaveable {
        mutableStateOf(
            listOf(
                Electrodomestico("Frigorífico"),
                Electrodomestico("Lavadora"),
                Electrodomestico("Microondas"),
                Electrodomestico("Plancha"),
                Electrodomestico("Lavavajillas"),
                Electrodomestico("Horno"),
                Electrodomestico("Secadora")
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
                items(items) { electrodomestico ->
                    // Card con botón de eliminar y opción para editar
                    CCard(
                        electrodomestico = electrodomestico,
                        onDelete = {
                            items = items.filter { it != electrodomestico } // Eliminar por objeto
                        },
                        onEdit = { updatedItem ->
                            items = items.map { if (it == electrodomestico) updatedItem else it }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(
                                8.dp,
                                shape = MaterialTheme.shapes.medium
                            ), // Sombra y borde redondeado
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = customContainerColor
                        ),
                        onFranjaClick = { franja ->
                            items = items.map {
                                if (it == electrodomestico) {
                                    it.copy(franjaHoraria = franja)
                                } else {
                                    it
                                }
                            }
                        }
                    )
                }
            }

            // Botón con icono y texto para agregar un electrodoméstico
            Button(
                onClick = {
                    if (nuevoItem.isNotBlank()) {
                        val electrodomestico = Electrodomestico(nuevoItem.trim())
                        items = items + electrodomestico
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
    electrodomestico: Electrodomestico,
    onDelete: () -> Unit,
    onEdit: (Electrodomestico) -> Unit,
    modifier: Modifier,
    shape: CornerBasedShape,
    colors: CardColors,
    onFranjaClick: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedElectrodomestico by remember { mutableStateOf(electrodomestico) }

    Card(
        modifier = modifier,
        shape = shape,
        colors = colors
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                TextField(
                    value = editedElectrodomestico.nombre,
                    onValueChange = {
                        editedElectrodomestico = editedElectrodomestico.copy(nombre = it)
                    },
                    label = { Text("Editar Electrodoméstico") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF016E6E),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = editedElectrodomestico.potencia.toString(),
                    onValueChange = {
                        editedElectrodomestico =
                            editedElectrodomestico.copy(potencia = it.toFloatOrNull() ?: 0f)
                    },
                    label = { Text("Editar Potencia (W)") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF016E6E),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = editedElectrodomestico.consumo.toString(),
                    onValueChange = {
                        editedElectrodomestico =
                            editedElectrodomestico.copy(consumo = it.toFloatOrNull() ?: 0f)
                    },
                    label = { Text("Editar Consumo (KWh)") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color(0xFF016E6E),
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            onEdit(editedElectrodomestico)
                            isEditing = false
                        },
                        enabled = editedElectrodomestico.nombre.isNotBlank()
                    ) {
                        Text("Guardar")
                    }
                    OutlinedButton(onClick = {
                        isEditing = false
                        editedElectrodomestico = electrodomestico
                    }) {
                        Text("Cancelar")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = electrodomestico.nombre,
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
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FranjaButton("00:00 - 02:00", electrodomestico.franjaHoraria, onFranjaClick)
                    FranjaButton("02:00 - 04:00", electrodomestico.franjaHoraria, onFranjaClick)
                    FranjaButton("04:00 - 06:00", electrodomestico.franjaHoraria, onFranjaClick)
                }
            }
        }
    }
}
    @Composable
    fun FranjaButton(franja: String, selectedFranja: String, onFranjaClick: (String) -> Unit) {
        Button(
            onClick = { onFranjaClick(franja) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (franja == selectedFranja) Color(0xFF016E6E) else Color.Gray
            )
        ) {
            Text(text = franja, color = Color.White)
        }
    }


@Preview(showBackground = true)
@Composable
fun PreviewInicioPantalla() {
    inicioPantalla()
}

package com.example.placas.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.material3.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.tooling.preview.Preview

data class Electrodomestico(
    var nombre: String,
    var potencia: Float = 0f,
    var consumo: Float = 0f,
    val franjasSeleccionadas: MutableSet<String> = mutableSetOf()
)

val franjasHorarias = listOf(
    "00:00 - 02:00", "02:00 - 04:00", "04:00 - 06:00",
    "06:00 - 08:00", "08:00 - 10:00", "10:00 - 12:00",
    "12:00 - 14:00", "14:00 - 16:00", "16:00 - 18:00",
    "18:00 - 20:00", "20:00 - 22:00", "22:00 - 00:00"
)

@Composable
fun InicioPantalla() {
    var items by rememberSaveable(
        stateSaver = listSaver(
            save = { list -> list.map { "${it.nombre}|${it.potencia}|${it.consumo}|${it.franjasSeleccionadas.joinToString(",")}" } },
            restore = { savedList ->
                savedList.map {
                    val parts = it.split("|")
                    Electrodomestico(
                        nombre = parts[0],
                        potencia = parts.getOrNull(1)?.toFloatOrNull() ?: 0f,
                        consumo = parts.getOrNull(2)?.toFloatOrNull() ?: 0f,
                        franjasSeleccionadas = parts.getOrNull(3)?.split(",")?.toMutableSet() ?: mutableSetOf()
                    )
                }
            }
        )
    ) {
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
    var textoBusqueda by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        Text(
            text = "Gestión de Electrodomésticos",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF03A9F4)
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = nuevoItem,
            onValueChange = { nuevoItem = it },
            placeholder = { Text("Nuevo electrodoméstico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium
        )


        AddElectrodomesticoButton {
            if (nuevoItem.isNotBlank()) {
                val updatedList = items.toMutableList()
                updatedList.add(Electrodomestico(nuevoItem.trim()))
                items = updatedList
                nuevoItem = ""
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            placeholder = { Text("Buscar electrodoméstico...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = MaterialTheme.shapes.medium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF016E6E),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        val itemsFiltrados = items.filter {
            it.nombre.contains(textoBusqueda, ignoreCase = true)
        }


        LazyColumn(modifier = Modifier.weight(1f)) {
            items(itemsFiltrados) { electrodomestico ->
            ElectrodomesticoCard(
                    electrodomestico = electrodomestico,
                    onDelete = {
                        items = items.filter { it != electrodomestico }
                    },
                    onEdit = { updated ->
                        items = items.map { if (it === electrodomestico) updated else it }
                    }
                )
            }
        }
    }
}

@Composable
fun AddElectrodomesticoButton(onAdd: () -> Unit) {
    Button(
        onClick = onAdd,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF016E6E))
    ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        Text("Agregar Electrodoméstico", color = Color.White, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun ElectrodomesticoCard(
    electrodomestico: Electrodomestico,
    onDelete: () -> Unit,
    onEdit: (Electrodomestico) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                EditElectrodomesticoForm(
                    electrodomestico = electrodomestico,
                    onSave = {
                        onEdit(it)
                        isEditing = false
                    },
                    onCancel = {
                        isEditing = false
                    }
                )
            } else {
                ViewElectrodomestico(
                    electrodomestico = electrodomestico,
                    onEdit = { isEditing = true },
                    onDelete = onDelete,
                    onFranjaClick = { onEdit(it) }
                )
            }
        }
    }
}

@Composable
fun EditElectrodomesticoForm(
    electrodomestico: Electrodomestico,
    onSave: (Electrodomestico) -> Unit,
    onCancel: () -> Unit
) {
    var nombre by remember { mutableStateOf(electrodomestico.nombre) }
    var potencia by remember { mutableStateOf(electrodomestico.potencia.toString()) }
    var consumo by remember { mutableStateOf(electrodomestico.consumo.toString()) }

    CampoTexto("Nombre", nombre) { nombre = it }
    CampoTexto("Potencia (W)", potencia) { potencia = it }
    CampoTexto("Consumo (KWh)", consumo) { consumo = it }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            onSave(
                electrodomestico.copy(
                    nombre = nombre,
                    potencia = potencia.toFloatOrNull() ?: 0f,
                    consumo = consumo.toFloatOrNull() ?: 0f
                )
            )
        }) {
            Text("Guardar")
        }

        Button(onClick = onCancel, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
            Text("Cancelar")
        }
    }
}

@Composable
fun ViewElectrodomestico(
    electrodomestico: Electrodomestico,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onFranjaClick: (Electrodomestico) -> Unit
) {
    Text(electrodomestico.nombre, fontSize = 18.sp, fontWeight = FontWeight.Medium)

    Spacer(modifier = Modifier.height(4.dp))

    Text("Potencia: ${electrodomestico.potencia} W | Consumo: ${electrodomestico.consumo} kWh")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onEdit) {
            Icon(Icons.Default.Edit, contentDescription = "Editar")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text("Selección de Franja Horaria", fontWeight = FontWeight.Bold, fontSize = 14.sp)

    var selectedFranjas by remember { mutableStateOf(electrodomestico.franjasSeleccionadas.toSet()) }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(franjasHorarias) { franja ->
            val isSelected = selectedFranjas.contains(franja)
            Button(
                onClick = {
                    selectedFranjas = if (isSelected) selectedFranjas - franja else selectedFranjas + franja
                    onFranjaClick(electrodomestico.copy(franjasSeleccionadas = selectedFranjas.toMutableSet()))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF016E6E) else Color.LightGray
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(franja, fontSize = 12.sp, color =Color(0xFF031B34))
            }
        }
    }
}

@Composable
fun CampoTexto(label: String, valor: String, onChange: (String) -> Unit) {
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = Color(0xFF016E6E),
            focusedIndicatorColor = Color(0xE8E950EF),
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


//
@Preview(showBackground = true)
@Composable
fun PreviewInicioPantalla() {
    InicioPantalla()
}

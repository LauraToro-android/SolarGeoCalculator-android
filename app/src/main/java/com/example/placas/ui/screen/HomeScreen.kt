package com.example.placas.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.placas.R



@Composable
fun HomeScreen(navController: NavController) {
    NestedScrolling()
}

const val motto: String = "Formación y Empleo"

@Composable
fun getCompanyName(): String {
    return stringResource(id = R.string.name_company)
}

@Composable
fun ShowTitle() {
    Spacer(modifier = Modifier.height(30.dp))
    Text(
        text = getCompanyName(),
        color = Color(0xFF043f70),
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
    )
    Text(
        text = motto
    )
}

@Composable
fun ShowBanner() {
    Box(
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Image(
            painterResource(R.drawable.portada),
            "banner")
    }
}

@Composable
fun NestedScrolling() {
    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ){
        item{
            ShowTitle()
        }
        item{
            ShowBanner()
        }
        item{
            Sublist1()
        }
        item {
            MainScreen()
        }
    }
}

@Composable
fun Sublist1() {
    val context = LocalContext.current // Get the context to show the Toast
    val imageList = listOf(
        R.drawable.tv2 to "Televisor",
        R.drawable.iron2 to "Plancha",
        R.drawable.dryer2 to "Secador",
        R.drawable.razor_blue2 to "Maquinilla azul",
        R.drawable.router2 to "Router",
        R.drawable.stove2 to "Estufa",
        R.drawable.vacuum2 to "Aspiradora",
        R.drawable.dryer_pink2 to "Secador rosado",
        R.drawable.razor_red2 to "Maquinilla roja"
    )

    val powerConsumption = mapOf(
        "Televisor" to 100,
        "Plancha" to 1200,
        "Secador" to 1500,
        "Maquinilla azul" to 10,
        "Router" to 15,
        "Estufa" to 2000,
        "Aspiradora" to 1400,
        "Secador rosado" to 1500,
        "Maquinilla roja" to 10
    )

    var selectedDevice by remember { mutableStateOf<String?>(null) }
    val deviceCountMap = remember { mutableStateMapOf<String, Int>() }
    val usageHoursMap = remember { mutableStateMapOf<String, String>() } // Save usage hours per device

    var dailySunHours by remember { mutableStateOf("4") }
    var panelPower by remember { mutableStateOf("400") }

    // Total consumption calculation
    val totalConsumption = deviceCountMap.entries.sumOf { (name, count) ->
        val usageHours = usageHoursMap[name]?.toIntOrNull() ?: 0
        (powerConsumption[name] ?: 0) * count * usageHours
    }

    // Calculate the number of panels required based on the total consumption and panel power
    val panelsRequired = if (dailySunHours.toFloatOrNull() != null && panelPower.toFloatOrNull() != null) {
        val productionPerPanel = (panelPower.toFloat()) * dailySunHours.toFloat()
        if (productionPerPanel > 0) totalConsumption / productionPerPanel else 0.0
    } else 0.0

    Column(modifier = Modifier.padding(16.dp)) {
        // Display device list in a horizontal scrollable row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(imageList) { (imageResId, deviceName) ->
                val isSelected = selectedDevice == deviceName

                // Display each device as a clickable card
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { selectedDevice = deviceName },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = deviceName,
                            modifier = Modifier.size(110.dp).padding(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show details for the selected device
        selectedDevice?.let { name ->
            val power = powerConsumption[name] ?: 0
            val count = deviceCountMap[name] ?: 0
            val usageHours = usageHoursMap[name] ?: "0"

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Seleccionado: $name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF163D6D)
                )

                Text(text = "Consumo por unidad por hora: $power W", style = MaterialTheme.typography.bodyMedium)

                // Input for daily usage hours
                OutlinedTextField(
                    value = usageHours,
                    onValueChange = { usageHoursMap[name] = it },
                    label = { Text("Horas de uso diario") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )

                // Buttons to increase or decrease the device count
                Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { deviceCountMap[name] = count + 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF163D6D),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Añadir")
                    }
                    Button(onClick = { if (count > 0) deviceCountMap[name] = count - 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF98133E),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Eliminar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Cantidad: $count", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show total consumption
        Text(text = "Consumo total diario: $totalConsumption Wh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Input for daily sun hours
        OutlinedTextField(
            value = dailySunHours,
            onValueChange = { dailySunHours = it },
            label = { Text("Horas de sol al día") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        // Input for solar panel power
        OutlinedTextField(
            value = panelPower,
            onValueChange = { panelPower = it },
            label = { Text("Potencia de la placa solar (W)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show the number of panels required
        Text(
            text = "Placas necesarias: ${String.format("%.2f", panelsRequired)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF163D6D),
            fontSize = 20.sp
        )

        // Button to reset all data
        Button(
            onClick = {
                // Reset all data
                selectedDevice = null
                deviceCountMap.clear()
                usageHoursMap.clear()
                dailySunHours = "4"
                panelPower = "400"

                // Show the Toast message
                Toast.makeText(context, "Datos reseteados", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF96133D),
                contentColor = Color.White
            )
        ) {
            Text("Resetear Todo")
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun ShowMyFirstColumn() {
    NestedScrolling()
}
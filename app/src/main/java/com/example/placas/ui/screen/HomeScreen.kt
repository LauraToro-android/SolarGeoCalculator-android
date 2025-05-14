package com.example.placas.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.placas.R
import androidx.compose.foundation.layout.PaddingValues
import com.example.placas.ui.components.DropDownMenu
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import com.example.placas.data.calculate.CalculoNPlacas
import com.example.placas.data.calculate.Soporte
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController){
    Scaffold(
        topBar = {Toolbar(navController)},
        content = { padding -> Content(
            padding,
            navController = navController
        ) }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(navController: NavController){
    TopAppBar(
        title = { Text(text = "Placas") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color.Transparent
        ),
        actions = { DropDownMenu(
            onSettingsClick = { navController.navigate("Settings") }
            , onLogOutClick = { navController.navigate("login") }) }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    paddingValues: PaddingValues,
    navController: NavController) {

    NestedScrolling()

}

@Composable
fun getCompanyName(): String {
    return stringResource(id = R.string.name_company)
}

@Composable
fun ShowTitle()
{
    Spacer(modifier = Modifier.height(30.dp))
    Row(
        modifier = Modifier
        .fillMaxWidth()
    ) { Text(
        text = getCompanyName(),
        color = Color(0xFF043f70),
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
    )
    }
}

@Composable
fun ShowBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)

    ) {
        Image(
            painter = painterResource(R.drawable.portada),
            contentDescription = "Banner",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun NestedScrolling() {

    LazyColumn (
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ){
        item{
            Spacer(modifier = Modifier.height(40.dp))
            ShowTitle(
            )
        }
        item{
            ShowBanner()
        }
        item {
            EnergyUsageScreen()
        }
        item {
            MainScreen()
        }

        /*item {
            RadiationCalculatorScreen()
        }*/
    }
}

@Composable
fun EnergyUsageScreen() {
    val context = LocalContext.current
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
    val hourRanges = (0 until 24 step 2).map { it to (it + 2) }
    var selectedRange by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val hourDeviceList = remember { mutableStateListOf<Triple<String, Pair<Int, Int>, Int>>() }

    var latitud by remember { mutableStateOf("41.553645") }
    var longitud by remember { mutableStateOf("-0.707426") }
    var angulo by remember { mutableStateOf("25") }
    var mes by remember { mutableStateOf("12") }
    var potenciaPlacaW by remember { mutableStateOf("500") }
    var margen by remember { mutableStateOf("0.8") }

    var numeroPlacas by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {

        // Selección de dispositivos
        Row( modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically)
        {
            Text("Selecciona un electrodoméstico:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(16.dp))

        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(imageList) { (imageResId, deviceName) ->
                val isSelected = selectedDevice == deviceName
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { selectedDevice = deviceName },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = deviceName,
                            modifier = Modifier.size(80.dp).padding(8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selección de rango horario
        Text("Selecciona la franja horaria:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(hourRanges) { range ->
                val isSelected = selectedRange == range
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(60.dp)
                        .clickable { selectedRange = range },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("${range.first}:00 - ${range.second}:00")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedDevice != null && selectedRange != null) {
                    hourDeviceList.add(Triple(selectedDevice!!, selectedRange!!, 1))
                    Toast.makeText(context, "Añadido correctamente", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xE1128D93),
                contentColor = Color.White
            )
        ) {
            Text("Añadir a la tabla")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tabla de dispositivos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        hourDeviceList.forEachIndexed { index, (name, range, count) ->
            val power = powerConsumption[name] ?: 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(name)
                    Text("${range.first}:00-${range.second}:00")
                }
                Text("x$count")
                Text("${power * count} W")
                IconButton(onClick = { hourDeviceList.removeAt(index) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        val grouped = hourDeviceList.groupBy { it.second }
        val maxEntry = grouped.maxByOrNull { entry ->
            entry.value.sumOf { (name, _, count) ->
                (powerConsumption[name] ?: 0) * count
            }
        }

        maxEntry?.let { (range, items) ->
            val total = items.sumOf { (name, _, count) -> (powerConsumption[name] ?: 0) * count }

            Soporte.energiaCalculada = total

            Text(
                text = "Mayor consumo en: ${range.first}:00-${range.second}:00 con $total W",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF163D6D)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Tipo de conexión", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        SwitchConection(modifier = Modifier)

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Cálculo de placas solares", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF163D6D))
                Spacer(modifier = Modifier.height(16.dp))

                ///parametros modificados para introducir a mano
                TextField(
                    value = angulo,
                    onValueChange = {
                        angulo = it
                        val valorInt = it.toIntOrNull()
                        if (valorInt != null) {
                            Soporte.anguloInclinacion = valorInt
                        }
                        Log.i("Test Angulo", "ANGULO: ${Soporte.anguloInclinacion}")
                    },
                    label = { Text("Ángulo de inclinación") }
                )

                TextField(
                    value = potenciaPlacaW,
                    onValueChange = {
                        potenciaPlacaW = it

                        val valorInt = it.toIntOrNull()
                        if (valorInt != null) {
                            Soporte.potenciaPlacaW = valorInt
                        }
                        Log.i("Test Potencia", "POTENCIA: ${Soporte.potenciaPlacaW}")
                    },
                    label = { Text("Potencia de placa (W)") }
                )

                //TextField(value = margen, onValueChange = { margen = it }, label = { Text("Margen (0-1)") })

                TextField(
                    value = margen,
                    onValueChange = {
                        margen = it

                        val valorDouble = it.toDoubleOrNull()
                        if (valorDouble != null && valorDouble in 0.0..1.0) {
                            Soporte.margen = valorDouble
                        }
                        Log.i("Test Potencia", "POTENCIA: ${Soporte.margen}")
                    },
                    label = { Text("Margen (0-1)") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Geocode() // Add location
            }

        }
    }
}

@Composable
fun ShowMyFirstColumn() {
    NestedScrolling()
}
@Composable
fun SwitchConection(modifier: Modifier = Modifier){
    var isChecked by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()

    ){
      Text(
          text = if (isChecked) "Aislado" else "Mixto",
      )
    }

    Switch(
        checked = isChecked,
        onCheckedChange = { isChecked = it },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Black,
            uncheckedThumbColor = Color.Black,
            checkedTrackColor = Color(0xE1128D93),
            uncheckedTrackColor = Color.LightGray),
        modifier = Modifier
            .scale(0.7f)

    )
}
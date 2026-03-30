package com.example.placas.ui.screen

import android.content.Intent
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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import com.example.placas.data.calculate.Soporte

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
            val energyViewModel: EnergyViewModel = viewModel()
            val navController: NavController = rememberNavController()
            EnergyUsageScreen(viewModel = energyViewModel, navController = navController)
            MainScreen()
        }
        //item {
        //    MainScreen()
        //}
        item {
            Feedback()
        }
    }
}

@Composable
fun EnergyUsageScreen(viewModel: EnergyViewModel, navController: NavController) {
    val context = LocalContext.current
    val imageList = listOf(
        R.drawable.tv2 to "Televisor",
        R.drawable.iron2 to "Plancha",
        R.drawable.razor_blue2 to "Maquinilla",
        R.drawable.router2 to "Router",
        R.drawable.stove2 to "Vitrocerámica",
        R.drawable.vacuum2 to "Aspiradora",
        R.drawable.dryer_pink2 to "Secador",
        R.drawable.frigorifico to "Frigorífico",
        R.drawable.lavadora to "Lavadora",
        R.drawable.bombilla to "Bonbilla",
        R.drawable.led to "Led",
        R.drawable.calefactor to "Calefactor",
        R.drawable.horno to "Horno",
        R.drawable.pc to "PC",
        R.drawable.videoconsola to "Videoconsola",
        R.drawable.lavavajillas to "Lavavajillas",
        R.drawable.termo to "Termotanque",
        R.drawable.aa to "Aire Acondicionado"

    )

    val powerConsumption = mapOf(
        "Televisor" to 100,
        "Plancha" to 1200,
        "Maquinilla" to 10,
        "Router" to 15,
        "Vitrocerámica" to 2000,
        "Aspiradora" to 1400,
        "Secador" to 1500,
        "Frigorífico" to 500,
        "Lavadora" to 2500,
        "Bombilla" to 60,
        "Led" to 12,
        "Calefactor" to 2500,
        "Horno" to 3000,
        "PC" to 800,
        "Videoconsola" to 220,
        "Lavavajillas" to 2000,
        "Termotanque" to 2500,
        "Aire Acondicionado" to 3000

    )

    var selectedDevice by remember { mutableStateOf<String?>(null) }
    val hourRanges = (0 until 24 step 2).map { it to (it + 2) }
    var selectedRange by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val hourDeviceList = remember { mutableStateListOf<Triple<String, Pair<Int, Int>, Int>>() }

    var angulo by remember { mutableStateOf("25") }
    var mes by remember { mutableStateOf("12") }
    var potenciaPlacaW by remember { mutableStateOf("500") }
    var margen by remember { mutableStateOf("0.8") }

    var mesError by remember { mutableStateOf(false) }
    var anguloError by remember { mutableStateOf(false) }
    var potenciaError by remember { mutableStateOf(false) }
    var margenError by remember { mutableStateOf(false) }

    var potenciaTouched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Soporte.mes = mes.toIntOrNull()
        Soporte.anguloInclinacion = angulo.toIntOrNull()
        Soporte.potenciaPlacaW = potenciaPlacaW.toIntOrNull()
        Soporte.margen = margen.toDoubleOrNull()
    }


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

        // Botón para añadir a la tabla actual (RAM)
        Button(
            onClick = {
                if (selectedDevice != null && selectedRange != null) {
                    viewModel.addDevice(Triple(selectedDevice!!, selectedRange!!, 1))
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

        // Mostrar tabla actual
        Text("Tabla de dispositivos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))
        if (viewModel.hourDeviceList.isEmpty()) {

            Text(
                text = "Seleccione los dispositivos que necesite en todas las franjas horarias que lo vaya a utilizar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )

        } else {

        var expanded by remember { mutableStateOf(false) }
        val maxItems = 6

        val visibleList = if (expanded) {
            viewModel.hourDeviceList
        } else {
            viewModel.hourDeviceList.take(maxItems)
        }


        visibleList.forEachIndexed { index, (name, range, count) ->
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
                    Text(getMergedRangeText(name, visibleList))

                }
                Text("x$count")
                Text("${power * count} W")
                IconButton(onClick = { viewModel.hourDeviceList.removeAt(index) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (viewModel.hourDeviceList.size > maxItems) {
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expanded) "Ver menos" else "Ver más")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Cálculo del mayor consumo
        val grouped = viewModel.hourDeviceList.groupBy { it.second }
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
                color = Color(0xFF043f70)
            )
            }
        }

        Row (horizontalArrangement = Arrangement.spacedBy(8.dp), // espacio entre botones
            modifier = Modifier.fillMaxWidth())
        {

            Button(onClick = {
                viewModel.clearTable()
            },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xE1128D93), contentColor = Color.White))
            {
                Text("Limpiar")
            }
        }


        Spacer(modifier = Modifier.height(20.dp))

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
                    value = mes,
                    onValueChange = {
                        mes = it
                        potenciaTouched = true
                        val valorInt = it.toIntOrNull()
                        mesError = it.isBlank() || (valorInt == null || valorInt !in 1..12)

                        Soporte.mes = valorInt

                        Log.i("Test Mes", "MES: ${Soporte.mes}")
                    },
                    label = { Text("Mes (1-12)") },
                    isError = potenciaTouched && mesError,
                    supportingText = {
                        if (potenciaTouched && mesError) {
                            if (mes.isBlank()) {
                                Text("Este campo es obligatorio")
                            } else {
                                Text("Introduce un mes válido entre 1 y 12")
                            }
                        }
                    }
                )
                TextField(
                    value = angulo,
                    onValueChange = {
                        angulo = it
                        potenciaTouched = true
                        val valorInt = it.toIntOrNull()
                        anguloError = it.isBlank() || (valorInt == null || valorInt !in 0..90)

                        Soporte.anguloInclinacion = valorInt

                        Log.i("Test Angulo", "ANGULO: ${Soporte.anguloInclinacion}")
                    },
                    label = { Text("Ángulo de inclinación (0-90)") },
                    isError = potenciaTouched && anguloError,
                    supportingText = {
                        if (potenciaTouched && anguloError) {
                            if (angulo.isBlank()) {
                                Text("Este campo es obligatorio")
                            } else {
                                Text("Introduce un ángulo válido entre 0 y 90")
                            }
                        }
                    }
                )

                TextField(
                    value = potenciaPlacaW,
                    onValueChange = {
                        potenciaPlacaW = it
                        potenciaTouched = true

                        val valorInt = it.toIntOrNull()

                        potenciaError =
                            it.isBlank() || (valorInt == null || valorInt !in 250..700)

                        Soporte.potenciaPlacaW = valorInt
                    },
                    label = { Text("Potencia de placa (W)") },
                    isError = potenciaTouched && potenciaError,
                    supportingText = {
                        if (potenciaTouched && potenciaError) {
                            if (potenciaPlacaW.isBlank()) {
                                Text("Este campo es obligatorio")
                            } else {
                                Text("Introduce una potencia entre 250 y 700")
                            }
                        }
                    }
                )

                TextField(
                    value = margen,
                    onValueChange = {
                        margen = it
                        potenciaTouched = true
                        val valorDouble = it.toDoubleOrNull()
                        margenError = it.isBlank() || (valorDouble == null || valorDouble !in 0.0..1.0)

                        Soporte.margen = valorDouble

                        Log.i("Test Potencia", "POTENCIA: ${Soporte.margen}")
                    },
                    label = { Text("Margen (0-1)") },
                    isError = potenciaTouched && margenError,
                    supportingText = {
                        if (potenciaTouched && margenError) {
                            if (margen.isBlank()) {
                                Text("Este campo es obligatorio")
                            } else {
                                Text("Introduce un margen válido entre 0 y 1")
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))




            }

        }
    }
}

@Composable
fun ShowMyFirstColumn() {
    NestedScrolling()
}

@Composable
fun Feedback() {
    val context = LocalContext.current
    val formUrl =
        "https://docs.google.com/forms/d/e/1FAIpQLSecbmLk06PGJJUoN7BuhtiZNbcx57od5ojs1rbIQE81aL21dg/viewform?usp=header"
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        "Danos tu Opinión",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF163D6D),
        fontSize = 25.sp
    )
    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, formUrl.toUri())
            context.startActivity(intent)
        },
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xE1128D93),
            contentColor = Color.White,

        )
    ) {
        Text("Formulario")
    }
}

fun getMergedRangeText(
    deviceName: String,
    list: List<Triple<String, Pair<Int, Int>, Int>>
): String {

    val ranges = list
        .filter { it.first == deviceName }
        .map { it.second }
        .sortedBy { it.first }

    if (ranges.isEmpty()) return ""

    var start = ranges.first().first
    var end = ranges.first().second

    for (i in 1 until ranges.size) {
        val current = ranges[i]
        if (current.first == end) {
            end = current.second
        } else {
            break
        }
    }

    return "$start:00-$end:00"
}




class EnergyViewModel : ViewModel() {
    var hourDeviceList = mutableStateListOf<Triple<String, Pair<Int, Int>, Int>>()
        private set

    fun addDevice(device: Triple<String, Pair<Int, Int>, Int>) {
        hourDeviceList.add(device)
    }

    fun removeDevice(index: Int) {
        if (index in hourDeviceList.indices) {
            hourDeviceList.removeAt(index)
        }
    }

    fun clearTable() {
        hourDeviceList.clear()
    }
}

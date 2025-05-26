package com.example.placas.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.placas.services.LocationIQService
import com.example.placas.services.RadiationService
import kotlinx.coroutines.launch


@Composable
fun MethodsScreen()
{
    WorstMonth()
}

@Composable
fun WorstMonth()
{
    var worstMonth by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var angle by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

/*
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Método del Mes Peor", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = lat,
                    onValueChange = { lat = it },
                    label = { Text("Latitud (Oscila entre -90° y +90°)") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = lon,
                    onValueChange = { lon = it },
                    label = { Text("Longitud (Oscila entre -180° y +180°)") }
                )
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = angle,
                    onValueChange = { angle = it },
                    label = { Text("Ángulo de inclinación de la placa solar") }
                )

                Button(onClick = {
                    // Llama a la API solo al pulsar el botón
                    if (LocationIQService.coordenadasValidas(lat.toDouble(), lon.toDouble()))
                    {
                        // Llama a la API solo al pulsar el botón
                        LocationIQService.reverseGeocode(lat, lon) { address ->
                            result ="Dirección: $address"
                        }

                        if(result == "")
                        {
                            result = "Has dado con agua"
                        }
                    }
                    else
                    {
                        result = "Coordenadas incorrectas"
                    }

                        coroutineScope.launch {
                            val resultado = RadiationService.fetchWorstMonthResult(
                                lat,
                                lon,
                                angle
                            )
                            if (resultado.isSuccess) {
                                worstMonth = resultado.getOrNull().orEmpty()
                                error = ""
                            } else {
                                error = resultado.exceptionOrNull()?.message.orEmpty()
                                worstMonth = ""
                            }
                        }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF98133E),
                    contentColor = Color.White
                )) {
                    Text("Consultar Mes Peor")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(result)
                if (worstMonth.isNotEmpty()) {
                    Text("Mes Peor: $worstMonth")
                }

                if (error.isNotEmpty()) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }*/
}
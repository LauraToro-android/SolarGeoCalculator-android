package com.example.placas.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class RadiationViewModel : ViewModel() {
    var latitude by mutableStateOf("")
    var longitude by mutableStateOf("")
    var annualRadiation by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun fetchRadiation() {
        if (latitude.isEmpty() || longitude.isEmpty()) {
            errorMessage = "Latitud y longitud son requeridas"
            return
        }

        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = ""

                val result = fetchRadiationData(latitude.toDouble(), longitude.toDouble())
                annualRadiation = result
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                annualRadiation = ""
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun fetchRadiationData(lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
        val url = "https://re.jrc.ec.europa.eu/api/MRcalc?lat=$lat&lon=$lon&horirrad=1&outputformat=json"

        val response = URL(url).readText()
        val jsonObject = JSONObject(response)

        // Extraer el valor anual de radiación horizontal
        val outputs = jsonObject.getJSONObject("outputs")
        val monthlyData = outputs.getJSONArray("monthly")

        // El último elemento contiene los totales anuales
        val yearlyData = monthlyData.getJSONObject(12)
        val radiationValue = yearlyData.getDouble("H(h)_m")

        return@withContext "$radiationValue kWh/m²"
    }
}

@Composable
fun RadiationCalculatorScreen(viewModel: RadiationViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Calculadora de Radiación Solar",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = viewModel.latitude,
            onValueChange = { viewModel.latitude = it },
            label = { Text("Latitud (ej: 40.416)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.longitude,
            onValueChange = { viewModel.longitude = it },
            label = { Text("Longitud (ej: -3.703)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.fetchRadiation() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        ) {
            Text("Obtener Radiación Anual")
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator()
        }

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        if (viewModel.annualRadiation.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Radiación Solar Anual:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = viewModel.annualRadiation,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}
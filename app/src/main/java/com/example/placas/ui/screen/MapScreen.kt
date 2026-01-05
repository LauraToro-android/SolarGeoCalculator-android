package com.example.placas.ui.screen

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.placas.data.calculate.CalculoNPlacas
import com.example.placas.services.LocationIQService
import com.example.placas.services.OpenStreetMapService
import com.example.placas.services.OpenStreetMapService.crearMapaConUbicacion
import com.example.placas.services.OpenStreetMapService.obtenerUbicacion
import com.example.placas.data.calculate.Soporte
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

/* ======================================================
   MAIN SCREEN
   ====================================================== */
@Composable
fun MainScreen() {

    // ✅ ESTADO REAL DE COMPOSE
    var mapLat by remember { mutableStateOf(Soporte.latitud ?: 40.4168) }
    var mapLon by remember { mutableStateOf(Soporte.longitud ?: -3.7038) }

    Column(modifier = Modifier.padding(16.dp)) {

        SolicitarPermisoUbicacion { lat, lon ->
            mapLat = lat
            mapLon = lon
            Soporte.latitud = lat
            Soporte.longitud = lon
        }

        Spacer(modifier = Modifier.height(12.dp))

        Geocode { lat, lon ->
            mapLat = lat
            mapLon = lon
            Soporte.latitud = lat
            Soporte.longitud = lon
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ✅ PASAMOS EL ESTADO REAL
        ReverseGeoCode(
            lat = mapLat,
            lon = mapLon
        ) { lat, lon ->
            mapLat = lat
            mapLon = lon
            Soporte.latitud = lat
            Soporte.longitud = lon
        }

        Spacer(modifier = Modifier.height(50.dp))

        OpenStreetMapViewWithUbication(mapLat, mapLon)

        Spacer(modifier = Modifier.height(58.dp))

        var resultadoPlacas by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                val lat = Soporte.latitud
                val lon = Soporte.longitud

                if (lat != null && lon != null) {
                    coroutineScope.launch {
                        val calculo = CalculoNPlacas(
                            latitud = lat,
                            longitud = lon,
                            anguloInclinacion = Soporte.anguloInclinacion,
                            mes = Soporte.mes ?: 12,
                            potenciaPlacaW = Soporte.potenciaPlacaW,
                            margen = Soporte.margen,
                            energiaCalculada = Soporte.energiaCalculada
                        )
                        resultadoPlacas = "Número de placas necesarias: ${calculo.calcularNumeroPlacas()}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xE1128D93),
                contentColor = Color.White
            )
        ) {
            Text("Calcular número de placas")
        }

        if (resultadoPlacas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(resultadoPlacas, fontWeight = FontWeight.Medium)
        }
    }
}

/* ======================================================
   GEOCODE
   ====================================================== */
@Composable
fun Geocode(onLocationSelected: (Double, Double) -> Unit) {

    var text by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var latD by remember { mutableStateOf("") }
    var lonD by remember { mutableStateOf("") }

    Column {
        Text("Buscar por dirección", fontWeight = FontWeight.Bold)

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                LocationIQService.geocode(text) { lat, lon, address ->
                    result = address
                    latD = lat
                    lonD = lon
                    lat.toDoubleOrNull()?.let { la ->
                        lon.toDoubleOrNull()?.let { lo ->
                            Soporte.latitud = la
                            Soporte.longitud = lo
                            onLocationSelected(la, lo)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar")
        }

        if (result.isNotEmpty()) {
            Text("Dirección: $result\nLatitud: $latD\nLongitud: $lonD")
        }
    }
}

/* ======================================================
   REVERSE GEOCODE (AHORA FUNCIONA)
   ====================================================== */
@Composable
fun ReverseGeoCode(
    lat: Double,
    lon: Double,
    onLocationSelected: (Double, Double) -> Unit
) {

    var latText by remember { mutableStateOf(lat.toString()) }
    var lonText by remember { mutableStateOf(lon.toString()) }
    var result by remember { mutableStateOf("") }

    // ✅ SE ACTUALIZA CUANDO GEOCODE CAMBIA
    LaunchedEffect(lat, lon) {
        latText = lat.toString()
        lonText = lon.toString()
    }

    Column {
        Text("Buscar por coordenadas", fontWeight = FontWeight.Bold)

        TextField(
            value = latText,
            onValueChange = { latText = it },
            label = { Text("Latitud") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = lonText,
            onValueChange = { lonText = it },
            label = { Text("Longitud") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val la = latText.toDoubleOrNull()
                val lo = lonText.toDoubleOrNull()
                if (la != null && lo != null) {
                    LocationIQService.reverseGeocode(
                        la.toString(),
                        lo.toString()
                    ) { address, apiLat, apiLon ->
                        result = address
                        onLocationSelected(
                            apiLat.toDoubleOrNull() ?: la,
                            apiLon.toDoubleOrNull() ?: lo
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar dirección")
        }

        if (result.isNotEmpty()) {
            Text("Dirección: $result")
        }
    }
}
//
/* ======================================================
   MAPA
   ====================================================== */
@Composable
fun OpenStreetMapViewWithUbication(lat: Double, lon: Double) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        OpenStreetMapService.initConfig(context)
    }

    key(lat, lon) {
        AndroidView(
            factory = { crearMapaConUbicacion(it, lat, lon) },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}

/* ======================================================
   PERMISOS
   ====================================================== */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SolicitarPermisoUbicacion(onLocationObtained: (Double, Double) -> Unit) {

    val permiso = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        permiso.launchPermissionRequest()
    }

    when {
        permiso.status.isGranted -> {
            LaunchedEffect(Unit) {
                obtenerUbicacion(context) {
                    onLocationObtained(it.latitude, it.longitude)
                }
            }
        }
        permiso.status.shouldShowRationale -> Text("Se necesita el permiso de ubicación")
        else -> Text("Permiso no concedido")
    }
}

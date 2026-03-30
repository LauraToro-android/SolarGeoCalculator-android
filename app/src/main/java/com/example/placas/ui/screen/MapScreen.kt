package com.example.placas.ui.screen

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.placas.data.calculate.CalculoNPlacas
import com.example.placas.services.LocationIQService
import com.example.placas.services.OpenStreetMapService
import com.example.placas.services.OpenStreetMapService.obtenerUbicacion
import com.example.placas.data.calculate.Soporte
import com.example.placas.data.calculate.Soporte.anguloInclinacion
import com.example.placas.data.calculate.Soporte.margen
import com.example.placas.data.calculate.Soporte.mes
import com.example.placas.data.calculate.Soporte.potenciaPlacaW
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

/* ======================================================
   MAIN SCREEN
   ====================================================== */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen() {

    // ✅ ESTADO REAL DE COMPOSE
    var mapLat by remember { mutableStateOf(Soporte.latitud ?: 40.4168) }
    var mapLon by remember { mutableStateOf(Soporte.longitud ?: -3.7038) }



    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {

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

        Spacer(modifier = Modifier.height(16.dp))

        OpenStreetMapViewWithUbication(mapLat, mapLon, modifier = Modifier
            .fillMaxWidth()
            .height(350.dp))

        Spacer(modifier = Modifier.height(24.dp))

        var resultadoPlacas by remember { mutableStateOf("") }
        var cargando by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val permiso = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

        Button(
            onClick = {
                if (permiso.status.isGranted) {
                    obtenerUbicacion(context) { location ->
                        val lat = location.latitude
                        val lon = location.longitude

                        mapLat = lat
                        mapLon = lon
                        Soporte.latitud = lat
                        Soporte.longitud = lon
                    }
                } else {
                    permiso.launchPermissionRequest()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xE1128D93),
                contentColor = Color.White
            )
        ) {
            Text("Usar mi ubicación 📍")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (cargando) return@Button

                // 🔹 Validación previa
                val errores = validarDatos()

                if (errores.isNotEmpty()) {
                    if (errores.size > 2) {
                        Toast.makeText(
                            context,
                            "Hay varios errores. Revisa los campos obligatorios.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            errores.joinToString("\n• ", prefix = "• "),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@Button
                }

                val lat = Soporte.latitud
                val lon = Soporte.longitud

                if (lat == null || lon == null) {
                    Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                cargando = true

                coroutineScope.launch {
                    try {
                        val calculo = CalculoNPlacas(
                            latitud = lat,
                            longitud = lon,
                            anguloInclinacion = Soporte.anguloInclinacion ?: 25,
                            mes = Soporte.mes ?: 12,
                            potenciaPlacaW = Soporte.potenciaPlacaW ?: 500,
                            margen = Soporte.margen ?: 0.8,
                            energiaCalculada = Soporte.energiaCalculada
                        )

                        val resultado = calculo.calcularNumeroPlacas()

                        if (resultado <= 0) {
                            Toast.makeText(context, "Resultado no válido", Toast.LENGTH_SHORT).show()
                        } else {
                            resultadoPlacas = "Número de placas necesarias: $resultado"
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error en el cálculo", Toast.LENGTH_LONG).show()
                    } finally {
                        cargando = false
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xE1128D93),
                contentColor = Color.White
            )
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Calcular número de placas")
            }
        }

// 🔹 RESULTADO (CORRECTO)
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

    var touched by remember { mutableStateOf(false) }
    var context = LocalContext.current






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
                touched = true
                result = ""

                LocationIQService.geocode(text) { lat, lon, address ->
                    val la = lat.toDoubleOrNull()
                    val lo = lon.toDoubleOrNull()

                    // 🔴 Si falla la búsqueda
                    if (la == null || lo == null || address.isEmpty()) {
                        result = "" // 🔥 importante para activar el error
                        return@geocode
                    }
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
            modifier = Modifier.fillMaxWidth(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xE1128D93), contentColor = Color.White)

        ) {
            Text("Buscar")
        }

        if (result.isNotEmpty()) {
            Text("Dirección: $result\nLatitud: $latD\nLongitud: $lonD")
        }
        if (touched && result.isEmpty()){
            Text("Dirección no válida", color = Color.Red)
        }
        Spacer(modifier = Modifier.height(16.dp))
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
    var latError by remember { mutableStateOf(false) }
    var lonError by remember { mutableStateOf(false) }
    var context = LocalContext.current





    // ✅ SE ACTUALIZA CUANDO GEOCODE CAMBIA
    LaunchedEffect(lat, lon) {
        latText = lat.toString()
        lonText = lon.toString()
        Soporte.latitud = latText.toDoubleOrNull()
        Soporte.longitud = lonText.toDoubleOrNull()

        latError = false
        lonError = false

    }

    Column {
        Text("Buscar por coordenadas", fontWeight = FontWeight.Bold)

        TextField(
            value = latText,
            onValueChange = {
                latText = it
                val latDouble = it.toDoubleOrNull()
                latError = it.isBlank() || (latDouble == null || latDouble !in -90.0..90.0)
                Soporte.latitud = latDouble

                Log.i("Test Latitud", "LATITUD: ${Soporte.latitud}")
                            },
            label = { Text("Latitud") },
            isError = latError,
            supportingText = {
                if (latError) {
                    Text("Introduce una latitud entre -90 y 90")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = lonText,
            onValueChange = {
                lonText = it
                val lonDouble = it.toDoubleOrNull()
                lonError = it.isBlank() || (lonDouble == null || lonDouble !in -180.0..180.0)
                Soporte.longitud = lonDouble

                Log.i("Test Longitud", "LONGITUD: ${Soporte.longitud}")
                            },
            label = { Text("Longitud") },
            isError = lonError,
            supportingText = {
                if (lonError) {
                    Text("Introduce una longitud entre -180 y 180")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val la = latText.toDoubleOrNull()
                val lo = lonText.toDoubleOrNull()
                val latValida = la != null && la in -90.0..90.0
                val lonValida = lo != null && lo in -180.0..180.0

                // 🔹 Recalculamos errores
                latError = !latValida
                lonError = !lonValida
                if (!latValida || !lonValida) {
                    Toast.makeText(context, "Coordenadas no válidas", Toast.LENGTH_SHORT).show()
                    return@Button
                }
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



            },
            modifier = Modifier.fillMaxWidth(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xE1128D93), contentColor = Color.White)
        ) {
            Text("Buscar")
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
fun OpenStreetMapViewWithUbication(lat: Double, lon: Double, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier
            .clipToBounds(),
        factory = {
            OpenStreetMapService.initConfig(context)
            OpenStreetMapService.crearMapaConUbicacion(context, lat, lon)
        },
        update = {
            OpenStreetMapService.actualizarUbicacion(lat, lon)
        }

    )
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
fun validarDatos(): List<String> {

    val errores = mutableListOf<String>()

    if (Soporte.energiaCalculada <= 0)
        errores.add("No hay electrodomésticos")

    if (Soporte.latitud == null || Soporte.longitud == null)
        errores.add("Ubicación no disponible")

    val mes = Soporte.mes
    if (mes == null) {
        errores.add("Mes vacío")
    } else if (mes !in 1..12) {
        errores.add("Mes inválido")
    }

    val angulo = Soporte.anguloInclinacion
    if (angulo == null) {
        errores.add("Ángulo vacío")
    } else if (angulo !in 0..90) {
        errores.add("Ángulo inválido")
    }

    val potencia = Soporte.potenciaPlacaW
    if (potencia == null) {
        errores.add("Potencia vacía")
    } else if (potencia <= 0) {
        errores.add("Potencia inválida")
    }

    val margen = Soporte.margen
    if (margen == null) {
        errores.add("Margen vacío")
    } else if (margen !in 0.0..1.0) {
        errores.add("Margen inválido")
    }

    return errores
}
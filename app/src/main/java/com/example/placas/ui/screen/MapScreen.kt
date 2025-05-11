package com.example.placas.ui.screen

import android.Manifest
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.placas.services.LocationIQService
import com.example.placas.services.OpenStreetMapService
import com.example.placas.services.OpenStreetMapService.crearMapaConUbicacion
import com.example.placas.services.OpenStreetMapService.obtenerUbicacion
import com.example.placas.services.RadiationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import com.example.placas.data.calculate.Soporte

import com.example.placas.data.calculate.CalculoNPlacas
import com.example.placas.data.calculate.Soporte.calcularMesSiEsNecesario

@Composable
fun MainScreen()
{
    Column(modifier = Modifier.padding(top = 30.dp))
    {
        Text(modifier = Modifier.padding(16.dp), text = "Mapa: ")
        SolicitarPermisoUbicacion()
        Spacer(modifier = Modifier.height(20.dp))
        Geocode()
        //Spacer(modifier = Modifier.height(20.dp))
        //ReverseGeoCode()
    }

}

@Composable
fun Geocode()
{
    var text by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var radiation by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Black),
        //shape = RoundedCornerShape(8.dp),
        tonalElevation = 4.dp,

        )
    {
        Column(modifier = Modifier.padding(20.dp))
        {
            Text("Introduce dirección: (Para verificar radiación anual solar)")
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = { text = it },
                label = { Text("Dirección") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (text.isNotBlank()) {
                            // Dirección introducida a mano
                            LocationIQService.geocode(text) { lat, lon, name ->
                                result = "Ubicación manual: $name\nLat: $lat, Lon: $lon"
                                Soporte.latitud = lat.toDoubleOrNull()
                                Soporte.longitud = lon.toDoubleOrNull()

                                launch {
                                    val peorMes = Soporte.calcularMesSiEsNecesario()
                                    if (peorMes != null) {
                                        calcularYMostrarResultado(
                                            lat = lat.toDoubleOrNull(),
                                            lon = lon.toDoubleOrNull(),
                                            peorMes = peorMes
                                        )
                                    } else {
                                        error = "Error al obtener el peor mes."
                                    }
                                }
                            }
                        } else {
                            // Ubicación del dispositivo
                            Soporte.obtenerUbicacionSiEsNecesario(context) {
                                val lat = Soporte.latitud
                                val lon = Soporte.longitud
                                if (lat != null && lon != null) {
                                    result = "Ubicación del dispositivo: Lat: $lat, Lon: $lon"

                                    launch {
                                        val peorMes = Soporte.calcularMesSiEsNecesario()
                                        if (peorMes != null) {
                                            calcularYMostrarResultado(
                                                lat = lat,
                                                lon = lon,
                                                peorMes = peorMes
                                            )
                                        } else {
                                            error = "Error al obtener el peor mes."
                                        }
                                    }
                                } else {
                                    error = "No se pudo obtener la ubicación del dispositivo."
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF98133E),
                    contentColor = Color.White
                )
            ) {
                Text("Calcular número de placas")
            }


            Spacer(modifier = Modifier.height(16.dp))
            Text(result)
            Spacer(modifier = Modifier.height(8.dp))
            if (radiation.isNotEmpty()) {
                Text("Radiación anual: $radiation")
            }

            if (error.isNotEmpty()) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }
        }

    }
}

@Composable
fun ReverseGeoCode()
{
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        border = BorderStroke(1.dp, Color.Black),
        //shape = RoundedCornerShape(8.dp),
        tonalElevation = 4.dp,
    )
    {
        Column(modifier = Modifier.padding(20.dp))
        {
            Text("Introduce coordenadas:")
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {

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

            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF98133E),
                contentColor = Color.White
            ))
            {
                Text("Buscar dirección")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(result)
        }

    }
}

@Composable
fun OpenStreetMapView()
{
    val context = LocalContext.current

    // Inicializa configuración solo una vez
    LaunchedEffect(Unit) {
        OpenStreetMapService.initConfig(context)
    }

    Surface(
        modifier = Modifier.padding(16.dp).fillMaxWidth().height(500.dp),
        border = BorderStroke(1.dp, Color.Black),
        //shape = RoundedCornerShape(8.dp),
        tonalElevation = 4.dp,
    )
    {
        AndroidView(
            factory = { context ->
                OpenStreetMapService.crearMapa(
                    context = context,
                    lat = 40.4168,
                    lon = -3.7038,
                    zoom = 15.0
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

}

@Composable
fun OpenStreetMapViewWithUbication(lat: Double, lon: Double)
{
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var radiation by remember { mutableStateOf("") }
    var resultCoord by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    // Inicializa configuración solo una vez
    LaunchedEffect(Unit) {
        OpenStreetMapService.initConfig(context)
    }

    Surface(
        modifier = Modifier.padding(16.dp).fillMaxWidth().height(500.dp),
        border = BorderStroke(1.dp, Color.Black),
        //shape = RoundedCornerShape(8.dp),
        tonalElevation = 4.dp,
    )
    {
        AndroidView(
            factory = {
                crearMapaConUbicacion(
                    context = it,
                    lat = lat,
                    lon = lon
                )

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
        )
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth())
    {
        Button( onClick = {
            // Llama a la API solo al pulsar el botón
            LocationIQService.reverseGeocode(lat.toString(), lon.toString()) { address ->
                direccion = address

                LocationIQService.geocode(direccion) { lat, lon, name ->
                    resultCoord = "Lat: $lat, Lon: $lon\nLugar: $name"
                }

                coroutineScope.launch {
                    val resultado = RadiationService.fetchRadiation(lat.toString(), lon.toString())
                    if (resultado.isSuccess) {
                        radiation = resultado.getOrNull().orEmpty()
                        error = ""
                    } else {
                        error = resultado.exceptionOrNull()?.message.orEmpty()
                        radiation = ""
                    }
                }
            }
        }, colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF98133E),
            contentColor = Color.White
        ))
        {
            Text("Consultar radiación solar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(resultCoord)
        Spacer(modifier = Modifier.height(8.dp))
        if (radiation.isNotEmpty()) {
            Text("Radiación anual: $radiation")
        }

        if (error.isNotEmpty()) {
            Text("Error: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)  // Habilitar la API experimental
@Composable
fun SolicitarPermisoUbicacion()
{
    val permisoUbicacion = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }

    // Lanzamos la solicitud de permiso al iniciar
    LaunchedEffect(Unit)
    {
        permisoUbicacion.launchPermissionRequest()
    }

    when
    {
        permisoUbicacion.status.isGranted ->
        {
            // Obtenemos ubicación solo si hay permiso
            LaunchedEffect(Unit)
            {
                obtenerUbicacion(context)
                {
                    location = it
                }
            }

            location?.let {
                OpenStreetMapViewWithUbication(it.latitude, it.longitude)
            } ?: OpenStreetMapView()
        }

        permisoUbicacion.status.shouldShowRationale ->
        {
            Text(modifier = Modifier.padding(16.dp), text = "Se necesita permiso de ubicación para centrar el mapa en tu posición.")
            OpenStreetMapView() // Mapa sin ubicación
        }

        else ->
        {
            Text(modifier = Modifier.padding(16.dp), text = "Permiso no concedido.")
            OpenStreetMapView() // Se muestra el mapa sin ubicación aquí
        }
    }
}

suspend fun calcularYMostrarResultado(
    lat: Double?,
    lon: Double?,
    peorMes: Int
) {
    var error: String = ""
    var radiation: String = ""
    if (lat == null || lon == null) {
        error = "Latitud o longitud no válidas."
        return
    }

    val calculador = CalculoNPlacas(
        latitud = lat,
        longitud = lon,
        anguloInclinacion = Soporte.anguloInclinacion,
        mes = peorMes,
        potenciaPlacaW = Soporte.potenciaPlacaW,
        margen = Soporte.margen,
        energiaCalculada = Soporte.energiaCalculada
    )

    val numeroPlacas = calculador.calcularNumeroPlacas()
    if (numeroPlacas > 0) {
        radiation = "Se requieren $numeroPlacas placas para cubrir la demanda en el peor mes."
        error = ""
    } else {
        radiation = ""
        error = "No se pudo calcular el número de placas."
    }
}
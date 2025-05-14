package com.example.placas.data.calculate

import android.Manifest
import android.util.Log
import com.example.placas.services.LocationIQService
import com.example.placas.services.OpenStreetMapService
import com.example.placas.services.RadiationService
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.placas.services.OpenStreetMapService.obtenerUbicacion
import com.example.placas.ui.screen.OpenStreetMapView
import com.example.placas.ui.screen.OpenStreetMapViewWithUbication
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Soporte {

    // Variables privadas para almacenar información de ubicación y parámetros de cálculo
    private var _latitud: Double? = null
    private var _longitud: Double? = null
    private var _anguloInclinacion: Int = 25
    private var _mes: Int? = null
    private var _potenciaPlacaW: Int = 500
    private var _margen: Double = 0.8
    private var _energiaCalculada: Int = 6000

    // Getters y Setters públicos
    var latitud: Double?
        get() = _latitud
        set(value) {
            _latitud = value
        }

    var longitud: Double?
        get() = _longitud
        set(value) {
            _longitud = value
        }

    var anguloInclinacion: Int
        get() = _anguloInclinacion
        set(value) {
            _anguloInclinacion = value
        }

    var mes: Int?
        get() = _mes
        set(value) {
            _mes = value
        }

    var potenciaPlacaW: Int
        get() = _potenciaPlacaW
        set(value) {
            _potenciaPlacaW = value
        }

    var margen: Double
        get() = _margen
        set(value) {
            _margen = value
        }

    var energiaCalculada: Int
        get() = _energiaCalculada
        set(value) {
            _energiaCalculada = value
        }

}

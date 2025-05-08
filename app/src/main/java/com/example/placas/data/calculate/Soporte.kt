package com.example.placas.data.calculate

import android.util.Log
import com.example.placas.services.LocationIQService
import com.example.placas.services.OpenStreetMapService
import com.example.placas.services.RadiationService
import android.annotation.SuppressLint
import android.content.Context

object Soporte {

    // Variables estáticas privadas
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

    /**
     * Intenta obtener la ubicación actual si no se ha establecido aún.
     * Utiliza OpenStreetMapService y LocationIQService para obtener lat/lon.
     */
    @SuppressLint("MissingPermission")
    fun obtenerUbicacionSiEsNecesario(context: Context, onCompletado: () -> Unit) {
        if (_latitud == null || _longitud == null) {
            OpenStreetMapService.obtenerUbicacion(context) { location ->
                val lat = location.latitude.toString()
                val lon = location.longitude.toString()

                LocationIQService.reverseGeocode(lat, lon) { _ ->
                    _latitud = location.latitude
                    _longitud = location.longitude
                    onCompletado()
                }
            }
        } else {
            onCompletado()
        }
    }

    /**
     * Calcula el peor mes de radiación si no está definido ya.
     * Utiliza la latitud, longitud y ángulo actuales.
     */
    suspend fun calcularMesSiEsNecesario(): Int? {
        return if (_mes == null && _latitud != null && _longitud != null) {
            try {
                val peorMes = RadiationService.fetchWorstMonth(
                    lat = _latitud!!,
                    lon = _longitud!!,
                    angle = _anguloInclinacion.toDouble()
                )
                _mes = peorMes.toInt()
                _mes
            } catch (e: Exception) {
                Log.e("Soporte", "Error al calcular peor mes: ${e.message}")
                null
            }
        } else {
            _mes
        }
    }
}
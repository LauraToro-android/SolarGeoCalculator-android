package com.example.placas.data.calculate

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.ceil
import io.ktor.serialization.kotlinx.json.json


@Serializable
data class DailyProfile(
    val month: Int,
    val time: String,
    @SerialName("G(i)") val Gi: Double = 0.0,
    @SerialName("Gb(i)") val Gbi: Double = 0.0,
    @SerialName("Gd(i)") val Gdi: Double = 0.0
)

@Serializable
data class Outputs(
    val daily_profile: List<DailyProfile>
)

@Serializable
data class PVResponse(
    val outputs: Outputs
)

class CalculoNPlacas(
    private val latitud: Double,
    private val longitud: Double,
    private val anguloInclinacion: Int,
    private val mes: Int,
    private val potenciaPlacaW: Int = 500,
    private val margen: Double = 0.8,
    private val energiaCalculada: Int = 6000
) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun calcularNumeroPlacas(): Int = withContext(Dispatchers.IO) {
        try {
            val url = "https://re.jrc.ec.europa.eu/api/v5_2/DRcalc"

            val response: PVResponse = client.get(url) {
                parameter("lat", latitud)
                parameter("lon", longitud)
                parameter("month", mes)
                parameter("angle", anguloInclinacion)
                parameter("global", 1) // << CLAVE
                parameter("startyear", 2023)
                parameter("outputformat", "json")
            }.body()

            val datos = response.outputs.daily_profile
            val energiaTotalDiaria = datos.sumOf { it.Gi } // Wh/m²/día
            val energiaPorPlaca = energiaTotalDiaria * (potenciaPlacaW / 1000.0) * margen
            Log.d("CalculoNPlacas", "Datos recibidos: $datos")

            if (energiaPorPlaca == 0.0) {
                throw Exception("La energía generada es 0.")
            }

            ceil(energiaCalculada / energiaPorPlaca).toInt()
        } catch (e: Exception) {
            Log.e("CalculoNPlacas", "Error: ${e.message}")
            -1
        }
    }
}
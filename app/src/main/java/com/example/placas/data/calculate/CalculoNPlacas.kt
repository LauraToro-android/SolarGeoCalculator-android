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

/** MODELOS **/

/**
 * Representa el perfil diario de radiación solar para un día del mes.
 *
 * @param month Mes correspondiente al dato (1 a 12).
 * @param time Hora del día en formato "HH:MM".
 * @param Gi Radiación global incidente en el plano inclinado (Wh/m²).
 * @param Gbi Radiación directa en el plano inclinado (Wh/m²).
 * @param Gdi Radiación difusa en el plano inclinado (Wh/m²).
 */

@Serializable
data class DailyProfile(
    val month: Int,
    val time: String,
    @SerialName("G(i)") val Gi: Double = 0.0,
    @SerialName("Gb(i)") val Gbi: Double = 0.0,
    @SerialName("Gd(i)") val Gdi: Double = 0.0
)

/**
 * Contenedor de la lista de perfiles diarios.
 */
@Serializable
data class Outputs(
    val daily_profile: List<DailyProfile>
)


/**
 * Modelo principal de la respuesta de la API PV (Paneles Fotovoltaicos).
 */
@Serializable
data class PVResponse(
    val outputs: Outputs
)

/**
 * Clase encargada de calcular el número necesario de placas solares
 * para cubrir una demanda energética mensual.
 *
 * @param latitud Latitud del sitio de instalación.
 * @param longitud Longitud del sitio de instalación.
 * @param anguloInclinacion Inclinación de las placas solares en grados.
 * @param mes Mes para el cual se realiza el cálculo (1-12).
 * @param potenciaPlacaW Potencia nominal de una placa en vatios (default 500W).
 * @param margen Eficiencia global del sistema (0 a 1), considerando pérdidas (default 80%).
 * @param energiaCalculada Energía deseada mensual en Wh (default 6000 Wh, equivalente a 6 kWh/día aprox).
 */
class CalculoNPlacas(
    private val latitud: Double,
    private val longitud: Double,
    private val anguloInclinacion: Int,
    private val mes: Int,
    private val potenciaPlacaW: Int = 500,
    private val margen: Double = 0.8,
    private val energiaCalculada: Int = 6000
) {
    // Cliente HTTP usando Ktor y deserialización automática con kotlinx.serialization
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

        /**
     * Función principal que realiza la llamada a la API de PV y calcula el número de placas necesarias.
     *
     * @return Número estimado de placas. Devuelve -1 si ocurre un error.
     */
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

            // Lista de valores horarios de radiación diaria
            val datos = response.outputs.daily_profile
             // Suma de radiación diaria total (Wh/m²/día) para
            val energiaTotalDiaria = datos.sumOf { it.Gi } // Wh/m²/día
             // Energía diaria generada por una placa: radiación * potencia * eficiencia
            val energiaPorPlaca = energiaTotalDiaria * (potenciaPlacaW / 1000.0) * margen
            Log.d("CalculoNPlacas", "Datos recibidos: $datos")

            if (energiaPorPlaca == 0.0) {
                throw Exception("La energía generada es 0.")
            }

            // Número de placas necesarias redondeado hacia arriba
            ceil(energiaCalculada / energiaPorPlaca).toInt()
        } catch (e: Exception) {
            Log.e("CalculoNPlacas", "Error: ${e.message}")
            -1
        }
    }
}